package com.ssafy.jjongle.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.jjongle.data.local.AuthDataSource
import com.ssafy.jjongle.domain.entity.AuthState
import com.ssafy.jjongle.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authPreferences: AuthDataSource // SharedPreferences를 통한 토큰 관리

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
            // Repository의 login 함수는 이미 내부에서 _authState의 isLoading을 true로 설정합니다.
            // 필요하다면 여기서도 UI 즉각 반응을 위해 설정할 수 있지만, 중복될 수 있습니다.
            // _authState.value = _authState.value.copy(isLoading = true, error = null)

            // AuthRepository의 login 함수는 Result<AuthState>를 직접 반환합니다.
            // ViewModel에서는 Repository의 _authState를 직접 구독하는 것이 아니라,
            // login 함수의 결과를 받아 처리 후 UI 상태를 업데이트합니다.
            // Repository에서 반환된 AuthState로 ViewModel의 _authState를 업데이트하거나,
            // 성공/실패에 따라 다른 처리를 합니다.

            val result = authRepository.login(idToken) // Repository의 login 호출

            result.onSuccess { newState ->
                // Repository에서 이미 _authState를 업데이트했을 것이므로,
                // ViewModel의 _authState를 여기서 또 업데이트할 필요는 없습니다.
                // Repository의 getAuthState()를 구독하고 있다면 자동으로 반영됩니다.
                // 단, Repository의 _authState와 ViewModel의 _authState가 동기화되도록
                // Repository의 getAuthState()를 ViewModel에서 collect 해야 합니다. (checkAuthStatus에서 이미 하고 있음)
                Log.d("AuthViewModel", "✅ 로그인 성공 access=${newState.accessToken}")
                // authState는 이미 Repository의 Flow를 통해 업데이트되므로, 여기서는 성공 콜백만 호출

                if (newState.isAuthenticated) {
                    Log.d(
                        "AuthViewModel",
                        "로그인 성공 후 상태: ${newState.user?.nickname} (${newState.user?.profileImage})"
                    )
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else {
                    Log.w("AuthViewModel", "로그인 성공했지만 인증되지 않은 상태: ${newState.error}")
                    withContext(Dispatchers.Main) {
                        onNeedSignUp() // 신규 유저 가입 유도
                    }

                }

            }.onFailure { throwable ->
                Log.e(
                    "AuthViewModel",
                    "Login onFailure. Throwable type: ${throwable::class.java.name}, Message: ${throwable.message}",
                    throwable
                )
                if (throwable is HttpException) {
                    Log.e("AuthViewModel", "❌ 로그인 실패 (기타 오류): ${throwable.message}")
                    // Repository에서 이미 AuthState의 error를 설정했을 것이므로,
                    // ViewModel의 _authState를 여기서도 업데이트 해줍니다.
                    // throwable.message가 null일 경우를 대비해 기본 메시지 제공
                    val errorMessage = throwable.message ?: "알 수 없는 오류로 로그인에 실패했습니다."
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isAuthenticated = false,
                        error = errorMessage
                    )
                    withContext(Dispatchers.Main) {
                        onFailure(throwable) // 일반 실패 처리
                    }
                }
                // isLoading은 Repository에서도 false로 설정되지만, ViewModel에서도 명시적으로 false로 설정
                // _authState.value = _authState.value.copy(isLoading = false) // 이미 위에서 처리됨
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
            // Repository의 signup 함수가 isLoading을 true로 설정할 것입니다.
            // _authState.value = _authState.value.copy(isLoading = true, error = null)

            val result = authRepository.signup(idToken, nickname, profileImage)

            result.onSuccess { newAuthState ->
                Log.d("AuthViewModel", "✅ 회원가입 성공! 새로운 상태: $newAuthState")
                // authState는 Repository의 Flow를 통해 업데이트됨
                // authPreferences는 Repository 내부의 LocalDataSource가 처리
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
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
                        withContext(Dispatchers.Main) {
                            onNeedLogin()
                        }
                        return@onFailure // 추가 처리 방지
                    }
                }
                // 기타 HTTP 오류 또는 일반 오류
                val errorMessage = throwable.message ?: "회원가입 중 알 수 없는 오류가 발생했습니다."
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = errorMessage
                )
                withContext(Dispatchers.Main) {
                    onFailure(throwable)
                }
            }
        }
    }


    // 🚪 로그아웃 처리
    // - 저장된 토큰 삭제 및 상태 초기화
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            // Repository에서 _authState를 초기화하므로, ViewModel에서 추가 작업 불필요.
            // _authState.value = AuthState() // 필요하다면 여기서도 UI 즉각 반응 위해 설정 가능
            Log.d("AuthViewModel", "Logout initiated. State will be updated via repository flow.")
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
            onSuccess()
        } catch (t: Throwable) {
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
            authPreferences.clearAuthData()   // 토큰/로컬 상태 정리
            onSuccess()
        } catch (t: Throwable) {
            onFailure(t)
        }
    }


    // 🔎 자동 로그인 여부 확인
    // - SharedPref에 access/refresh token이 있는지 확인
    // - 있으면 로그인 상태로 전환
    fun checkAuthStatus() {
        viewModelScope.launch {
            // Repository의 checkAuthStatus는 내부적으로 _authState를 업데이트합니다.
            // ViewModel은 Repository의 getAuthState() Flow를 구독하여 상태를 받습니다.
            authRepository.checkAuthStatus() // 로컬 토큰 확인 및 Repository의 _authState 업데이트 요청
            authRepository.getAuthState().collect { state -> // Repository의 상태 변경 구독
                if (_authState.value != state) { // 실제 변경이 있을 때만 업데이트 (불필요한 리컴포지션 방지)
                    _authState.value = state
                    Log.d(
                        "AuthViewModel_checkAuthStatus",
                        "Auth state collected: isAuthenticated=${state.isAuthenticated}, isLoading=${state.isLoading}, User=${state.user?.nickname}, Error=${state.error}"
                    )
                }
            }
            // try-catch는 Repository 내부에서 처리하는 것이 더 적절할 수 있습니다.
            // 여기서는 getAuthState().collect 자체의 예외를 처리할 수 있습니다.
        }
    }
}
