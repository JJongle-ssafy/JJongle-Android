package com.ssafy.jjongle.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.jjongle.domain.entity.AuthState
import com.ssafy.jjongle.domain.entity.UserInfo
import com.ssafy.jjongle.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // 🔄 로그인 상태 관리용 StateFlow (Compose UI에 반영됨)
    private val _authState = MutableStateFlow(AuthState(isLoading = true))
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // 앱 시작 시 자동 로그인 여부 확인
        checkAuthStatus()
    }

    // 🔐 서버 로그인 요청
    // - 구글 로그인으로 받은 idToken을 서버에 보내 인증 요청
    // - 성공 시 토큰 저장 및 로그인 상태 갱신
    fun login(
        idToken: String,
        onSuccess: () -> Unit,
        onNeedSignUp: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            val result = authRepository.login(idToken)
            result.onSuccess { newState ->
                _authState.value = newState.copy(isLoading = false, error = null)
                Log.d("AuthViewModel", "로그인 성공. hasAccessToken=${!newState.accessToken.isNullOrBlank()}")

                if (newState.isAuthenticated) {
                    Log.d(
                        "AuthViewModel",
                        "로그인 성공 후 상태: ${newState.user?.nickname} (${newState.user?.profileImage})"
                    )
                    onSuccess()
                } else {
                    Log.w("AuthViewModel", "로그인 성공했지만 인증되지 않은 상태: ${newState.error}")
                    onNeedSignUp() // 신규 유저 가입 유도
                }

            }.onFailure { throwable ->
                Log.e(
                    "AuthViewModel",
                    "Login onFailure. Throwable type: ${throwable::class.java.name}, Message: ${throwable.message}",
                    throwable
                )
                val errorMessage = throwable.message ?: "알 수 없는 오류로 로그인에 실패했습니다."
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    isAuthenticated = false,
                    error = errorMessage
                )
                onFailure(throwable)
            }
        }
    }


    // 📝 서버 회원가입 요청
    // - 닉네임 + 캐릭터 이미지 + idToken을 보내 회원가입 처리
    // - 서버에서 토큰을 응답받아 로그인 상태 갱신
    fun signUp(
        idToken: String,
        nickname: String,
        profileImage: String,
        onSuccess: () -> Unit = {},
        onFailure: (Throwable) -> Unit = {},
        onNeedLogin: () -> Unit = {}  // ✅ 409 시 로그인 유도 콜백

    ) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            val result = authRepository.signup(idToken, nickname, profileImage)

            result.onSuccess { newAuthState ->
                _authState.value = newAuthState.copy(isLoading = false, error = null)
                Log.d("AuthViewModel", "✅ 회원가입 성공! 새로운 상태: $newAuthState")
                onSuccess()
            }.onFailure { throwable ->
                Log.e(
                    "AuthViewModel",
                    "회원가입 onFailure. Throwable: ${throwable::class.java.name}, Message: ${throwable.message}",
                    throwable
                )
                if (throwable is HttpException) {
                    val code = throwable.code()
                    val errorBody =
                        throwable.response()?.errorBody()?.string() // 에러 바디는 한 번만 읽을 수 있으므로 주의

                    Log.e(
                        "AuthViewModel", """
                        ❌ 회원가입 실패 (HTTP)
                        🔸 코드: $code
                        🔹 메시지: ${throwable.message()}
                        🔹 바디 (추정): $errorBody 
                    """.trimIndent()
                    ) // errorBody는 로깅 후에는 다시 읽을 수 없을 수 있음

                    if (code == 409) { // 이미 가입된 유저 (서버 정책에 따름)
                        Log.w("AuthViewModel", "❗️409 Conflict → 이미 가입된 유저. 로그인 유도")
                        _authState.value = _authState.value.copy(
                            isLoading = false,
                            error = throwable.message() ?: "이미 가입된 사용자입니다. 로그인을 시도해주세요."
                        )
                        onNeedLogin()
                        return@onFailure // 추가 처리 방지
                    }
                }
                // 기타 HTTP 오류 또는 일반 오류
                val errorMessage = throwable.message ?: "회원가입 중 알 수 없는 오류가 발생했습니다."
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = errorMessage
                )
                onFailure(throwable)
            }
        }
    }


    // 🚪 로그아웃 처리
    // - 저장된 토큰 삭제 및 상태 초기화
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = AuthState(isAuthenticated = false, isLoading = false)
            Log.d("AuthViewModel", "Logout completed")
        }
    }


    // 🔄 프로필 업데이트
    fun updateProfile(
        nickname: String,
        profileImage: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) = viewModelScope.launch {
        try {
            authRepository.updateProfile(nickname, profileImage) // ← Repository 구현 필요
            val current = _authState.value
            _authState.value = current.copy(
                user = current.user?.copy(
                    nickname = nickname,
                    profileImage = profileImage
                ) ?: UserInfo(
                    userId = 0L,
                    email = null,
                    nickname = nickname,
                    profileImage = profileImage
                ),
                error = null
            )
            onSuccess()
        } catch (t: Throwable) {
            _authState.value = _authState.value.copy(error = t.message)
            onFailure(t)
        }
    }


    // 🔒 회원 탈퇴 처리
    fun withdraw(
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) = viewModelScope.launch {
        try {
            authRepository.withdraw() // Repository 구현 필요
            _authState.value = AuthState(isAuthenticated = false, isLoading = false)
            onSuccess()
        } catch (t: Throwable) {
            _authState.value = _authState.value.copy(error = t.message)
            onFailure(t)
        }
    }


    // 🔎 자동 로그인 여부 확인
    // - SharedPref에 access/refresh token이 있는지 확인
    // - 있으면 로그인 상태로 전환
    fun checkAuthStatus() {
        viewModelScope.launch {
            try {
                val state = authRepository.checkAuthStatus()
                _authState.value = state.copy(isLoading = false, error = null)
                Log.d(
                    "AuthViewModel_checkAuthStatus",
                    "Auth state restored: isAuthenticated=${state.isAuthenticated}, User=${state.user?.nickname}"
                )
            } catch (t: Throwable) {
                _authState.value = AuthState(
                    isAuthenticated = false,
                    isLoading = false,
                    error = t.message ?: "로그인 상태 확인 실패"
                )
            }
        }
    }
}
