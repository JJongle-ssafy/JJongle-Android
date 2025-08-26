package com.ssafy.jjongle.data.remote

import com.ssafy.jjongle.data.remote.model.AuthTokenResponse
import com.ssafy.jjongle.data.remote.model.LogInRequest
import com.ssafy.jjongle.data.remote.model.SignUpRequest
import com.ssafy.jjongle.data.remote.model.UserUpdateRequest
import retrofit2.Response

interface AuthRemoteDataSource {
    suspend fun login(request: LogInRequest): Response<AuthTokenResponse>
    suspend fun signup(request: SignUpRequest): Response<AuthTokenResponse>     // retrofit의 Response 타입을 사용하여 HTTP 상태 코드와 헤더를 포함
    suspend fun reissue(refreshToken: String): Response<Unit>
    suspend fun updateUser(body: UserUpdateRequest): Response<Unit>
    suspend fun deleteUser(): Response<Unit>
}
