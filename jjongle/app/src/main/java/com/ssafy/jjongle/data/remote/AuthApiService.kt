package com.ssafy.jjongle.data.remote

import com.ssafy.jjongle.data.remote.model.AuthTokenResponse
import com.ssafy.jjongle.data.remote.model.LogInRequest
import com.ssafy.jjongle.data.remote.model.SignUpRequest
import com.ssafy.jjongle.data.remote.model.UserUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT


// retrofit 인터페이스 정의
// API 요청을 정의하는 인터페이스
interface AuthApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LogInRequest
    ): Response<AuthTokenResponse>

    @POST("auth/signup")
    suspend fun signup(
        @Body request: SignUpRequest
    ): Response<AuthTokenResponse>

    @POST("auth/reissue")
    suspend fun reissueTokenByCookie(
        @Header("Cookie") cookie: String
    ): Response<Unit> // 서버 응답 타입에 맞춰 유지

    @PUT("/user")
    suspend fun updateUser(@Body body: UserUpdateRequest): Response<Unit> // Swagger에 응답 스키마 없음 → Unit

    @DELETE("/user")
    suspend fun deleteUser(): Response<Unit>
}