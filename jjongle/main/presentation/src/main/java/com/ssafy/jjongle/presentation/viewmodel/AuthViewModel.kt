package com.ssafy.jjongle.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.ssafy.jjongle.common.domain.error.auth.AuthException
import com.ssafy.jjongle.common.entity.AuthState
import com.ssafy.jjongle.common.domain.error.auth.MissingTokenAuthError
import com.ssafy.jjongle.common.entity.UserInfo
import com.ssafy.jjongle.common.domain.error.auth.UserAlreadyExistsAuthError
import com.ssafy.jjongle.common.domain.repository.GoogleAuthService
import com.ssafy.jjongle.common.domain.usecase.AuthUseCase
import com.ssafy.jjongle.common.presentation.mvi.MviViewModel
import com.ssafy.jjongle.presentation.state.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val googleAuthService: GoogleAuthService
) : MviViewModel<AuthIntent, AuthUiState, AuthReducerEvent>(AuthUiState.empty) {

    init {
        onIntent(AuthIntent.CheckAuthStatus)
    }

    override fun onIntent(intent: AuthIntent) {
        when (intent) {
            AuthIntent.CheckAuthStatus -> checkAuthStatus()
            AuthIntent.Logout -> logout()
            is AuthIntent.ShowError -> dispatch(AuthReducerEvent.Failed(intent.message))
            is AuthIntent.LoginWithGoogleIdToken -> loginWithGoogleIdToken(
                googleIdToken = intent.googleIdToken,
                onSuccess = intent.onSuccess,
                onNeedSignUp = intent.onNeedSignUp,
                onFailure = intent.onFailure,
            )
            is AuthIntent.SignUp -> signUp(
                idToken = intent.idToken,
                nickname = intent.nickname,
                profileImage = intent.profileImage,
                onSuccess = intent.onSuccess,
                onFailure = intent.onFailure,
                onNeedLogin = intent.onNeedLogin,
            )
            is AuthIntent.UpdateProfile -> updateProfile(
                nickname = intent.nickname,
                profileImage = intent.profileImage,
                onSuccess = intent.onSuccess,
                onFailure = intent.onFailure,
            )
            is AuthIntent.Withdraw -> withdraw(
                onSuccess = intent.onSuccess,
                onFailure = intent.onFailure,
            )
        }
    }

    override fun reduce(state: AuthUiState, event: AuthReducerEvent): AuthUiState {
        val authState = when (event) {
            AuthReducerEvent.LoadingStarted -> state.authState.copy(isLoading = true, error = null)
            is AuthReducerEvent.StateChanged -> event.authState
            is AuthReducerEvent.Failed -> state.authState.copy(
                isLoading = false,
                isAuthenticated = false,
                error = event.message,
            )
        }
        return state.copy(authState = authState)
    }

    // Google/Firebase 로그인 후 Firestore 사용자 프로필 존재 여부로 인증 상태를 갱신
    private fun login(
        idToken: String,
        onSuccess: () -> Unit,
        onNeedSignUp: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            dispatch(AuthReducerEvent.LoadingStarted)

            val result = authUseCase.login(idToken)
            result.onSuccess { newState ->
                dispatch(AuthReducerEvent.StateChanged(newState.copy(isLoading = false, error = null)))

                if (newState.isAuthenticated) {
                    onSuccess()
                } else {
                    onNeedSignUp() // 신규 유저 가입 유도
                }

            }.onFailure { throwable ->
                val errorMessage = throwable.message ?: "알 수 없는 오류로 로그인에 실패했습니다."
                dispatch(AuthReducerEvent.Failed(errorMessage))
                onFailure(throwable)
            }
        }
    }

    private fun loginWithGoogleIdToken(
        googleIdToken: String,
        onSuccess: () -> Unit,
        onNeedSignUp: (String) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            dispatch(AuthReducerEvent.LoadingStarted)

            runCatching { googleAuthService.signIn(googleIdToken) }
                .onSuccess { googleUser ->
                    val firebaseIdToken = googleUser.idToken
                    if (firebaseIdToken.isNullOrBlank()) {
                        val error = AuthException(
                            MissingTokenAuthError,
                            "Firebase ID 토큰을 가져오지 못했습니다."
                        )
                        dispatch(AuthReducerEvent.Failed(error.message.orEmpty()))
                        onFailure(error)
                        return@launch
                    }

                    login(
                        idToken = firebaseIdToken,
                        onSuccess = onSuccess,
                        onNeedSignUp = { onNeedSignUp(firebaseIdToken) },
                        onFailure = onFailure
                    )
                }
                .onFailure { throwable ->
                    dispatch(AuthReducerEvent.Failed(throwable.message ?: "Google 인증에 실패했습니다."))
                    onFailure(throwable)
                }
        }
    }


    // Firestore 사용자 프로필을 생성해 회원가입 처리
    private fun signUp(
        idToken: String,
        nickname: String,
        profileImage: String,
        onSuccess: () -> Unit = {},
        onFailure: (Throwable) -> Unit = {},
        onNeedLogin: () -> Unit = {}  // ✅ 409 시 로그인 유도 콜백

    ) {
        viewModelScope.launch {
            dispatch(AuthReducerEvent.LoadingStarted)

            val result = authUseCase.signup(idToken, nickname, profileImage)

            result.onSuccess { newAuthState ->
                dispatch(AuthReducerEvent.StateChanged(newAuthState.copy(isLoading = false, error = null)))
                onSuccess()
            }.onFailure { throwable ->
                if (throwable is AuthException && throwable.error is UserAlreadyExistsAuthError) {
                    dispatch(
                        AuthReducerEvent.StateChanged(
                            currentState.authState.copy(
                                isLoading = false,
                                error = throwable.message,
                            )
                        )
                    )
                    onNeedLogin()
                    return@onFailure
                }
                // 기타 HTTP 오류 또는 일반 오류
                val errorMessage = throwable.message ?: "회원가입 중 알 수 없는 오류가 발생했습니다."
                dispatch(
                    AuthReducerEvent.StateChanged(
                        currentState.authState.copy(
                            isLoading = false,
                            error = errorMessage,
                        )
                    )
                )
                onFailure(throwable)
            }
        }
    }


    // 로그아웃 처리
    // - Firebase 세션과 로컬 인증 캐시 초기화
    private fun logout() {
        viewModelScope.launch {
            authUseCase.logout()
                .onSuccess {
                    googleAuthService.signOut()
                    dispatch(AuthReducerEvent.StateChanged(AuthState(isAuthenticated = false, isLoading = false)))
                }
                .onFailure { throwable ->
                    dispatch(AuthReducerEvent.Failed(throwable.message ?: "로그아웃 실패"))
                }
        }
    }


    // 🔄 프로필 업데이트
    private fun updateProfile(
        nickname: String,
        profileImage: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) = viewModelScope.launch {
        authUseCase.updateProfile(nickname, profileImage)
            .onSuccess {
                val current = currentState.authState
                dispatch(
                    AuthReducerEvent.StateChanged(
                        current.copy(
                            user = current.user?.copy(
                                nickname = nickname,
                                profileImage = profileImage
                            ) ?: UserInfo(
                                userId = 0L,
                                email = UserInfo.MISSING_EMAIL,
                                nickname = nickname,
                                profileImage = profileImage
                            ),
                            error = null
                        )
                    )
                )
                onSuccess()
            }
            .onFailure { throwable ->
                dispatch(AuthReducerEvent.StateChanged(currentState.authState.copy(error = throwable.message)))
                onFailure(throwable)
            }
    }


    // 🔒 회원 탈퇴 처리
    private fun withdraw(
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) = viewModelScope.launch {
        authUseCase.withdraw()
            .onSuccess {
                dispatch(AuthReducerEvent.StateChanged(AuthState(isAuthenticated = false, isLoading = false)))
                onSuccess()
            }
            .onFailure { throwable ->
                dispatch(AuthReducerEvent.StateChanged(currentState.authState.copy(error = throwable.message)))
                onFailure(throwable)
            }
    }


    // 자동 로그인 여부 확인
    // - Firebase current user와 Firestore 프로필을 기준으로 로그인 상태 복원
    private fun checkAuthStatus() {
        viewModelScope.launch {
            authUseCase.checkAuthStatus()
                .onSuccess { state ->
                    dispatch(AuthReducerEvent.StateChanged(state.copy(isLoading = false, error = null)))
                }
                .onFailure { throwable ->
                    dispatch(AuthReducerEvent.Failed(throwable.message ?: "로그인 상태 확인 실패"))
                }
        }
    }
}
