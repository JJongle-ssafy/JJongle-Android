package com.ssafy.jjongle.tangram.data.remote

import com.ssafy.jjongle.tangram.data.remote.model.SingleGameDTO
import com.ssafy.jjongle.tangram.data.remote.model.TangramDetailDTO
import com.ssafy.jjongle.tangram.data.remote.model.TangramHistoriesPageDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Tangram Game API 호출을 Retrofit 메서드로 정의하는 네트워크 계약입니다.
 *
 * HTTP 경로와 요청/응답 DTO 형태를 data 계층 안에 모아 domain 모델이 서버 스키마에 직접 의존하지 않게 합니다.
 */
interface TangramGameApiService {
    @GET("/single-game/histories")
    suspend fun getTangramHistories(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<TangramHistoriesPageDTO>

    @GET("/single-game/history/{tangramId}")
    suspend fun getTangramDetail(@Path("tangramId") id: Long): Response<TangramDetailDTO>

    @GET("single-game")
    suspend fun getSingleGame(): Response<SingleGameDTO>
}
