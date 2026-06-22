package com.ssafy.jjongle.data.remote

import android.util.Log
import com.ssafy.jjongle.data.remote.model.AuthTokenResponse
import com.ssafy.jjongle.data.remote.model.LogInRequest
import com.ssafy.jjongle.data.remote.model.SignUpRequest
import com.ssafy.jjongle.data.remote.model.UserUpdateRequest
import retrofit2.Response


class AuthRemoteDataSourceImpl(
    private val authApiService: AuthApiService
) : AuthRemoteDataSource {

    // 로그인 API 호출
    override suspend fun login(request: LogInRequest): Response<AuthTokenResponse> {

        Log.d("AuthRemoteDataSource", "로그인 API 요청. hasFirebaseIdToken=${request.firebaseIdToken.isNotBlank()}")

        val response = authApiService.login(request)
        Log.d("AuthRemoteDataSource", "로그인 API 응답 code=${response.code()}")
        return response
    }

    // 회원가입 API 호출
    override suspend fun signup(request: SignUpRequest): Response<AuthTokenResponse> {

        Log.d("AuthRemoteDataSource", "회원가입 API 요청. hasFirebaseIdToken=${request.firebaseIdToken.isNotBlank()}")
        val response = authApiService.signup(request)

        Log.d("AuthRemoteDataSource", "회원가입 API 응답 code=${response.code()}")
        return response
    }

    // 리프레시 토큰을 사용하여 새로운 액세스 토큰 발급
    override suspend fun reissue(refreshToken: String): Response<Unit> {
        return authApiService.reissueTokenByCookie(
            cookie = "refreshToken=$refreshToken"
        )
    }


    // 유저 프로필 업데이트
    override suspend fun updateUser(body: UserUpdateRequest): Response<Unit> =
        authApiService.updateUser(body)

    // 유저 탈퇴
    override suspend fun deleteUser(): Response<Unit> =
        authApiService.deleteUser()

}
