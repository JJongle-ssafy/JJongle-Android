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

    // 로그인: 서버에 로그인 요청 → 토큰 저장 → 인증 결과 반환
    override suspend fun login(idToken: String): Result<AuthState> {
        return try {
            val response = authRemoteDataSource.login(LogInRequest(idToken))
            if (!response.isSuccessful) throw HttpException(response)

            val body = response.body() ?: return Result.failure(Exception("로그인 응답 바디 없음"))
            persistTokensFromHeaders(response)
            val accessToken = authDataSource.getAccessToken()
            val refreshToken = authDataSource.getRefreshToken()
            if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
                throw IllegalStateException("로그인 응답에 토큰이 없습니다.")
            }

            authDataSource.saveUserProfile(body.nickname, body.profileImage)

            val state = AuthState(
                isAuthenticated = true,
                accessToken = accessToken,
                refreshToken = refreshToken,
                user = UserInfo(
                    userId = 0L,
                    email = "",
                    nickname = body.nickname,
                    profileImage = body.profileImage
                ),
                isLoading = false
            )

            Result.success(state)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                Log.w("AuthRepositoryImpl", "로그인 시 신규회원으로 처리: ${e.message()}")
                return Result.success(AuthState(isAuthenticated = false, isLoading = false))
            }

            Log.e("AuthRepositoryImpl", "로그인 중 HttpException 발생: ${e.code()} ${e.message()}", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "로그인 중 일반 Exception 발생", e)
            Result.failure(e)
        }
    }


    // 회원가입: 서버에 가입 요청 → 토큰 저장 → 인증 결과 반환
    override suspend fun signup(
        idToken: String,
        nickname: String,
        profileImage: String
    ): Result<AuthState> {
        return try {
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
            persistTokensFromHeaders(response)
            val accessToken = authDataSource.getAccessToken()
            val refreshToken = authDataSource.getRefreshToken()
            if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
                throw IllegalStateException("회원가입 응답에 토큰이 없습니다.")
            }
            Log.d("AuthRemoteDataSource", "Tokens saved")

            val newState = AuthState(
                isAuthenticated = true,
                accessToken = accessToken,
                refreshToken = refreshToken,
                user = UserInfo(
                    userId = 0L,
                    email = "",
                    nickname = body.nickname ?: nickname,
                    profileImage = body.profileImage ?: profileImage
                ),
                isLoading = false
            )
            Result.success(newState)

        } catch (e: HttpException) {
            Log.e("AuthRepositoryImpl", "회원가입 중 HttpException 발생: ${e.code()} ${e.message()}", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "회원가입 중 일반 Exception 발생", e)
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

            persistTokensFromHeaders(response)
            val newAccessToken = authDataSource.getAccessToken()
            val newRefreshToken = authDataSource.getRefreshToken()

            if (newAccessToken.isNullOrBlank() || newRefreshToken.isNullOrBlank()) {
                Log.e("AuthRepositoryImpl", "🚫 토큰 재발급 후 토큰 없음")
                if (response.code() == 401 || response.code() == 403) logout() // 재발급 실패로 간주하고 로그아웃
                throw Exception("토큰 재발급 후 서버로부터 토큰을 받지 못했습니다.")
            }

            val updatedState = AuthState(
                isAuthenticated = true,
                accessToken = newAccessToken,
                refreshToken = newRefreshToken,
                user = getCachedUser(),
                isLoading = false
            )

            Result.success(updatedState)

        } catch (e: HttpException) {
            Log.e("AuthRepositoryImpl", "토큰 재발급 중 HttpException 발생: ${e.code()} ${e.message()}", e)
            if (e.code() == 401 || e.code() == 403) {
                logout() // 토큰이 더 이상 유효하지 않으므로 로그아웃
            }
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "토큰 재발급 중 일반 Exception 발생", e)
            Result.failure(e)
        }
    }

    // 회원 정보 수정: 서버에 요청 → 로컬 데이터 저장
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
        } catch (e: Exception) {
            android.util.Log.e("AuthRepositoryImpl", "회원정보 수정 실패", e)
            throw e // ViewModel로 예외 전달
        }
    }


    // 회원 탈퇴: 서버에 요청 → 로컬 데이터 삭제
    override suspend fun withdraw() {
        try {
            val res = authRemoteDataSource.deleteUser() // DELETE /user
            if (!res.isSuccessful) throw HttpException(res)

            authDataSource.clearAuthData()
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "회원 탈퇴 실패", e)
            throw e
        }
    }


    // 로그아웃: 저장된 토큰 삭제
    override suspend fun logout() {
        authDataSource.clearAuthData()
        Log.d("AuthRepositoryImpl", "로그아웃 완료: 토큰 삭제")
    }


    // 초기 앱 실행 시, 저장된 토큰 유무로 로그인 상태 판단
    override suspend fun checkAuthStatus(): AuthState {
        val accessToken = authDataSource.getAccessToken()
        val refreshToken = authDataSource.getRefreshToken()
        val isLoggedIn = !accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()

        Log.d("AuthRepositoryImpl_checkAuthStatus", "hasAccessToken=${!accessToken.isNullOrBlank()}")
        Log.d("AuthRepositoryImpl_checkAuthStatus", "hasRefreshToken=${!refreshToken.isNullOrBlank()}")
        Log.d("AuthRepositoryImpl_checkAuthStatus", "isLoggedIn=$isLoggedIn")

        if (isLoggedIn) {
            return AuthState(
                isAuthenticated = true,
                accessToken = accessToken,
                refreshToken = refreshToken,
                user = getCachedUser(),
                isLoading = false
            )
        }

        return AuthState(isAuthenticated = false, isLoading = false)
    }

    private fun getCachedUser(): UserInfo? {
        val nickname = authDataSource.getNickname()
        val profile = authDataSource.getProfileImage()
        return if (!nickname.isNullOrBlank() && !profile.isNullOrBlank()) {
            UserInfo(userId = 0L, email = null, nickname = nickname, profileImage = profile)
        } else {
            null
        }
    }

    private fun persistTokensFromHeaders(response: Response<*>) {
        val accessToken = response.headers()["Authorization"]
        val setCookieHeaders = response.headers().values("Set-Cookie")
        val refreshToken = setCookieHeaders.firstOrNull { it.startsWith("refreshToken=") }
            ?.substringAfter("refreshToken=")
            ?.substringBefore(";")
            ?: response.headers()["Refresh-Token"]

        if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
            throw IllegalStateException("인증 응답 헤더에 토큰이 없습니다.")
        }

        authDataSource.saveTokens(accessToken, refreshToken)
        authDataSource.saveSetCookies(setCookieHeaders)
    }
}
