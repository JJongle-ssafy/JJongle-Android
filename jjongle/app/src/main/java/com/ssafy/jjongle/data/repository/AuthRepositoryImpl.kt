package com.ssafy.jjongle.data.repository

import android.util.Log
import com.ssafy.jjongle.data.local.AuthDataSource
import com.ssafy.jjongle.data.remote.AuthRemoteDataSource
import com.ssafy.jjongle.data.remote.model.LogInRequest
import com.ssafy.jjongle.data.remote.model.SignUpRequest
import com.ssafy.jjongle.data.remote.model.UserUpdateRequest
import com.ssafy.jjongle.domain.entity.AuthState
import com.ssafy.jjongle.domain.entity.UserInfo
import com.ssafy.jjongle.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val authDataSource: AuthDataSource
) : AuthRepository {

    private val _authState = MutableStateFlow(AuthState())
    override fun getAuthState(): Flow<AuthState> = _authState.asStateFlow()

    // 로그인: 서버에 로그인 요청 → 토큰 저장 → 상태 업데이트
    override suspend fun login(idToken: String): Result<AuthState> {
        return try {
            _authState.value = _authState.value.copy(isLoading = true, error = null) // 로딩 상태 시작

            val response = authRemoteDataSource.login(LogInRequest(idToken))
            if (!response.isSuccessful) throw HttpException(response)

            // 이미 저장해둔 토큰을 꺼내옵니다.
            val accessToken = authDataSource.getAccessToken()
            val refreshToken = authDataSource.getRefreshToken()
            if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
                throw HttpException(Response.error<Any>(401, "...".toResponseBody()))
            }

            // 서버 응답에서 사용자 정보를 가져옵니다.
            val body = response.body() ?: return Result.failure(Exception("로그인 응답 바디 없음"))
            // 응답에 들어온 사용자 정보를 로컬에 저장합니다.
            authDataSource.saveUserProfile(body.nickname, body.profileImage)


            val state = AuthState(
                isAuthenticated = true, // 성공적으로 토큰을 받았으므로 true
                accessToken = accessToken,
                refreshToken = refreshToken,
                user = UserInfo(
                    userId = 0L, // 서버 응답에 userId가 있다면 사용
                    email = "",  // 서버 응답에 email이 있다면 사용
                    nickname = body.nickname,
                    profileImage = body.profileImage
                ),
                isLoading = false
            )

            _authState.value = state
            Result.success(state)
        } catch (e: HttpException) {
            // HttpException은 그대로 전달하여 ViewModel에서 코드별로 처리할 수 있도록 함
            // 401이면 신규회원으로 처리, 에러 아님
            if (e.code() == 401) {
                Log.w("AuthRepositoryImpl", "로그인 시 신규회원으로 처리: ${e.message()}")
                _authState.value = _authState.value.copy(isAuthenticated = false, isLoading = false)
                return Result.success(_authState.value) // 신규회원 상태로 성공 처리
            }

            Log.e("AuthRepositoryImpl", "로그인 중 HttpException 발생: ${e.code()} ${e.message()}", e)
            _authState.value = _authState.value.copy(isLoading = false, error = e.message())
            Result.failure(e)
        } catch (e: Exception) {
            // 기타 예외 (네트워크 오류 등)
            Log.e("AuthRepositoryImpl", "로그인 중 일반 Exception 발생", e)
            _authState.value = _authState.value.copy(
                isLoading = false,
                error = e.message ?: "알 수 없는 오류 발생"
            )
            Result.failure(e)
        }
    }


    // 회원가입: 서버에 가입 요청 → 토큰 저장 → 상태 업데이트
    override suspend fun signup(
        idToken: String,
        nickname: String,
        profileImage: String
    ): Result<AuthState> {
        return try {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            val response = authRemoteDataSource.signup(
                SignUpRequest(
                    firebaseIdToken = idToken,
                    nickname = nickname,
                    profileImage = profileImage
                )
            )

            if (!response.isSuccessful) {
                Log.e(
                    "AuthRepositoryImpl",
                    "Signup API 에러: ${response.code()} ${response.message()}"
                )
                val errorBody = response.errorBody()
                    ?: "".toResponseBody("application/json".toMediaTypeOrNull())
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }

            val body = response.body() ?: throw Exception("회원가입 응답 바디 없음")

            val accessToken = authDataSource.getAccessToken()
            val refreshToken = authDataSource.getRefreshToken()

            if (!accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()) {
                Log.d("AuthRemoteDataSource", "✅ Tokens saved: $accessToken / $refreshToken")
            } else {
                Log.w("AuthRemoteDataSource", "Tokens not found in headers: ${response.headers()}")
            }

            // 새로운 AuthState 생성
            val newState = AuthState(
                isAuthenticated = true,
                accessToken = accessToken,
                refreshToken = refreshToken,
                user = UserInfo(
                    userId = 0L, // 서버 응답에 userId가 있다면 사용
                    email = "", // 서버 응답에 email이 있다면 사용
                    nickname = body.nickname ?: nickname, // 응답 바디에 nickname이 있을 경우 사용, 없으면 요청값 사용
                    profileImage = body.profileImage
                        ?: profileImage // 응답 바디에 profileImage가 있을 경우 사용, 없으면 요청값 사용
                ),
                isLoading = false
            )
            _authState.value = newState
            Result.success(newState)

        } catch (e: HttpException) {
            Log.e("AuthRepositoryImpl", "회원가입 중 HttpException 발생: ${e.code()} ${e.message()}", e)
            val errorState = _authState.value.copy(
                isLoading = false,
                error = e.message() ?: "회원가입 실패 (HTTP 오류)"
            )
            _authState.value = errorState
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "회원가입 중 일반 Exception 발생", e)
            val errorState = _authState.value.copy(
                isLoading = false,
                error = e.message ?: "회원가입 실패"
            )
            _authState.value = errorState
            Result.failure(e)
        }
    }


    // 토큰 재발급: refreshToken → 서버 요청 → 새 토큰 저장
    override suspend fun reissue(refreshToken: String): Result<AuthState> {
        // 기존 refreshToken이 유효하지 않으면 재발급 시도하지 않음
        val currentRefreshToken = authDataSource.getRefreshToken()
        if (currentRefreshToken.isNullOrBlank() || currentRefreshToken != refreshToken) {
            Log.w("AuthRepositoryImpl", "저장된 리프레시 토큰이 없거나 일치하지 않아 재발급 요청을 중단합니다.")
            logout() // 안전하게 로그아웃 처리
            return Result.failure(Exception("유효한 리프레시 토큰이 없습니다."))
        }

        return try {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            val response = authRemoteDataSource.reissue(currentRefreshToken) // 로컬에 저장된 토큰 사용

            if (!response.isSuccessful) {
                Log.e(
                    "AuthRepositoryImpl",
                    "Reissue API 에러: ${response.code()} ${response.message()}"
                )
                // 재발급 실패 시 (예: 401 - 만료된 리프레시 토큰), 로그아웃 처리
                if (response.code() == 401 || response.code() == 403) {
                    logout()
                }
                val errorBody = response.errorBody()
                    ?: "".toResponseBody("application/json".toMediaTypeOrNull())
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }


            val newAccessToken = response.headers()["accessToken"]
            // 재발급 시 새로운 리프레시 토큰을 받을 수도 있고, 기존 것을 계속 사용할 수도 있음 (서버 정책에 따라)
            // 여기서는 새로운 리프레시 토큰도 받는다고 가정
            val newRefreshToken =
                response.headers()["refreshToken"] ?: currentRefreshToken // 새 리프레시 토큰이 없으면 기존 것 유지

            if (newAccessToken.isNullOrBlank()) {
                Log.e("AuthRepositoryImpl", "🚫 토큰 재발급 후 accessToken 없음")
                if (response.code() == 401 || response.code() == 403) logout() // 재발급 실패로 간주하고 로그아웃
                throw Exception("토큰 재발급 후 서버로부터 accessToken을 받지 못했습니다.")
            }

            val updatedState = _authState.value.copy(
                isAuthenticated = true,
                accessToken = newAccessToken,
                refreshToken = newRefreshToken,
                isLoading = false
            )

            _authState.value = updatedState
            Result.success(updatedState)

        } catch (e: HttpException) {
            Log.e("AuthRepositoryImpl", "토큰 재발급 중 HttpException 발생: ${e.code()} ${e.message()}", e)
            _authState.value = _authState.value.copy(isLoading = false, error = e.message())
            // 특정 에러 코드(예: 401, 403) 발생 시 로그아웃 처리
            if (e.code() == 401 || e.code() == 403) {
                logout() // 토큰이 더 이상 유효하지 않으므로 로그아웃
            }
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "토큰 재발급 중 일반 Exception 발생", e)
            _authState.value =
                _authState.value.copy(isLoading = false, error = e.message ?: "토큰 재발급 실패")
            Result.failure(e)
        }
    }

    // 회원 정보 수정: 서버에 요청 → 로컬 데이터 저장 + 상태 업데이트
    override suspend fun updateProfile(nickname: String, profileImage: String) {
        try {
            val res = authRemoteDataSource.updateUser(
                UserUpdateRequest(
                    nickname = nickname,
                    profileImage = profileImage // "DEFAULT" | "MONGI" | "TOBY" | "LUNA"
                )
            )
            if (!res.isSuccessful) throw HttpException(res)

            // 로컬 동기화
            authDataSource.saveUserProfile(nickname, profileImage)

            // 메모리 상태 동기화
            _authState.value = _authState.value.copy(
                user = _authState.value.user?.copy(
                    nickname = nickname,
                    profileImage = profileImage
                ) ?: UserInfo(
                    userId = 0L,
                    email = null,
                    nickname = nickname,
                    profileImage = profileImage
                )
            )
        } catch (e: Exception) {
            // 필요 시 로그 추가
            android.util.Log.e("AuthRepositoryImpl", "회원정보 수정 실패", e)
            throw e // ViewModel로 예외 전달
        }
    }


    // 회원 탈퇴: 서버에 요청 → 로컬 데이터 삭제 + 상태 초기화
    override suspend fun withdraw() {
        try {
            val res = authRemoteDataSource.deleteUser() // DELETE /user
            if (!res.isSuccessful) throw HttpException(res)

            // 로컬 정리 + 상태 초기화
            authDataSource.clearAuthData()
            _authState.value = AuthState(isAuthenticated = false, isLoading = false, user = null)
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "회원 탈퇴 실패", e)
            throw e
        }
    }


    // 로그아웃: 저장된 토큰 삭제 + 상태 초기화
    override suspend fun logout() {
        authDataSource.clearAuthData()
        _authState.value = AuthState(isAuthenticated = false, isLoading = false) // 명시적으로 로그아웃 상태 설정
        Log.d("AuthRepositoryImpl", "로그아웃 완료: 토큰 삭제 및 상태 초기화")
    }


    // 초기 앱 실행 시, 저장된 토큰 유무로 로그인 상태 판단 및 사용자 정보 로드 시도
    override suspend fun checkAuthStatus() {
        val accessToken = authDataSource.getAccessToken()
        val refreshToken = authDataSource.getRefreshToken()
        val isLoggedIn = !accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()

        Log.d("AuthRepositoryImpl_checkAuthStatus", "🔍 accessToken: $accessToken")
        Log.d("AuthRepositoryImpl_checkAuthStatus", "🔍 refreshToken: $refreshToken")
        Log.d("AuthRepositoryImpl_checkAuthStatus", "🔍 isLoggedIn: $isLoggedIn")

        if (isLoggedIn) {
            // 토큰이 존재하는 경우, 사용자 정보를 로드하거나 상태 업데이트
            val nickname = authDataSource.getNickname()
            val profile = authDataSource.getProfileImage()

            // preferences에 저장된 사용자 정보를 읽어옴
            val restoredUser =
                if (!nickname.isNullOrBlank() && !profile.isNullOrBlank())
                    UserInfo(userId = 0L, email = null, nickname = nickname, profileImage = profile)
                else _authState.value.user


            // 토큰이 있다면, 사용자 정보를 로드하거나, 최소한 토큰 정보로 AuthState 업데이트
            // 실제로는 여기서 사용자 정보를 서버에서 가져오거나 로컬에서 로드하는 로직이 추가될 수 있음
            // 예시: val userInfo = authLocalDataSource.getUserInfo()
            // 지금은 토큰만으로 상태를 설정합니다. UserInfo는 로그인/회원가입 시 채워집니다.
            _authState.value = AuthState(
                isAuthenticated = true,
                accessToken = accessToken,
                refreshToken = refreshToken,
                user = restoredUser,
                isLoading = false
            )
            // 필요하다면 여기서 토큰 유효성 검사 (예: 매우 짧은 만료 시간을 가진 access token의 경우 시작 시 재발급 시도)
            // validateAndRefreshTokensIfNeeded()
        } else {
            _authState.value = AuthState(isAuthenticated = false, isLoading = false)
        }
    }
}
