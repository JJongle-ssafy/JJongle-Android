package com.ssafy.jjongle.data.remote

import android.util.Log
import com.ssafy.jjongle.data.local.AuthDataSource
import com.ssafy.jjongle.data.remote.model.AuthTokenResponse
import com.ssafy.jjongle.data.remote.model.LogInRequest
import com.ssafy.jjongle.data.remote.model.SignUpRequest
import com.ssafy.jjongle.data.remote.model.UserUpdateRequest
import retrofit2.Response


class AuthRemoteDataSourceImpl(
    private val authApiService: AuthApiService,
    private val authDataSource: AuthDataSource // ✨ accessToken, refreshToken 받기위한 주입
) : AuthRemoteDataSource {

    // 로그인 API 호출
    override suspend fun login(request: LogInRequest): Response<AuthTokenResponse> {

        Log.d("AuthRemoteDataSource", "🔗 로그인 API 요청: $request")

        val response = authApiService.login(request)
        Log.d("AuthRemoteDataSource", "🔗 로그인 API 응답: $response")
        // 성공(2xx)일 때만 헤더에서 토큰 추출
        if (response.isSuccessful) {
            Log.d("AuthRemoteDataSource", "🔗 로그인 성공, 헤더에서 토큰 추출")

            // 응답이 null인 경우 예외 처리
            if (response.body() == null) {
                throw Exception("로그인 응답이 비어있습니다.")
            }

            // 헤더에서 토큰을 추출하여 로컬 데이터 소스에 저장
            // 둘 다 null/blank가 아니면 한 번에 저장
            val headerAccessToken = response.headers()["Authorization"]
            Log.d("AuthRemoteDataSource", "🔗 헤더에서 accessToken: $headerAccessToken")

            // 쿠키에서 refreshToken 추출
            val setCookieHeaders = response.headers().values("Set-Cookie")
            val refreshToken = setCookieHeaders.firstOrNull { it.startsWith("refreshToken=") }
                ?.substringAfter("refreshToken=")?.substringBefore(";")

            if (!headerAccessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()) {
                authDataSource.saveTokens(headerAccessToken, refreshToken)
                authDataSource.saveSetCookies(setCookieHeaders) // ★ 추가
            }
        }
        return response
    }

    // 회원가입 API 호출
    override suspend fun signup(request: SignUpRequest): Response<AuthTokenResponse> {

        Log.d("AuthRemoteDataSource", "🆕 회원가입 API 요청: $request")
        val response = authApiService.signup(request)

        Log.d("AuthRemoteDataSource", "🆕 회원가입 API 응답: $response")
        if (response.isSuccessful) {
            Log.d("AuthRemoteDataSource", "🆕 회원가입 성공, 헤더에서 토큰 추출")

            // 헤더에서 토큰을 추출하여 로컬 데이터 소스에 저장
            // 둘 다 null/blank가 아니면 한 번에 저장
            if (response.body() == null) {
                throw Exception("회원가입 응답이 비어있습니다.")
            }

            // 쿠키에서 refreshToken 추출
            val headerAccessToken = response.headers()["Authorization"]
            val setCookieHeaders = response.headers().values("Set-Cookie")
            val refreshToken = setCookieHeaders.firstOrNull { it.startsWith("refreshToken=") }
                ?.substringAfter("refreshToken=")?.substringBefore(";")

            if (!headerAccessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()) {
                authDataSource.saveTokens(headerAccessToken, refreshToken)
                authDataSource.saveSetCookies(setCookieHeaders) // ★ 추가
            }
        }
        return response
    }

    // 리프레시 토큰을 사용하여 새로운 액세스 토큰 발급
    override suspend fun reissue(refreshToken: String): Response<Unit> {
        // 쿠키만 보내기
        val response = authApiService.reissueTokenByCookie(
            cookie = "refreshToken=$refreshToken"
        )

        // 새 access
        val newAccessToken = response.headers()["Authorization"]
            ?: throw Exception("accessToken 없음")

        // 새 refresh: Set-Cookie에서 refreshToken= 추출 (없으면 헤더 보조)
        val setCookies = response.headers().values("Set-Cookie")
        authDataSource.saveSetCookies(setCookies)

        val cookieRefresh = setCookies
            .firstOrNull { it.startsWith("refreshToken=") }
            ?.substringAfter("refreshToken=")
            ?.substringBefore(";")
        val newRefreshToken = cookieRefresh
            ?: response.headers()["Refresh-Token"]
            ?: throw Exception("refreshToken 없음")

        // (선택) Set-Cookie 전체 저장 로직이 있다면 여기서 갱신
        // authDataSource.saveSetCookies(setCookies)

        authDataSource.saveTokens(newAccessToken, newRefreshToken)
        return response
    }


    // 유저 프로필 업데이트
    override suspend fun updateUser(body: UserUpdateRequest): Response<Unit> =
        authApiService.updateUser(body)

    // 유저 탈퇴
    override suspend fun deleteUser(): Response<Unit> =
        authApiService.deleteUser()

}
