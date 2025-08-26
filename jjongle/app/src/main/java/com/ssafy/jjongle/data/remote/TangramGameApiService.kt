package com.ssafy.jjongle.data.remote

import com.ssafy.jjongle.data.remote.model.SingleGameResponse
import com.ssafy.jjongle.data.remote.model.TangramDetailResponse
import com.ssafy.jjongle.data.remote.model.TangramHistoriesPageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TangramGameApiService {
    @GET("/single-game/histories")
    suspend fun getTangramHistories(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): TangramHistoriesPageResponse

    @GET("/single-game/history/{tangramId}")
    suspend fun getTangramDetail(@Path("tangramId") id: Long): TangramDetailResponse

    @GET("single-game")
    suspend fun getSingleGame(): Response<SingleGameResponse>
}