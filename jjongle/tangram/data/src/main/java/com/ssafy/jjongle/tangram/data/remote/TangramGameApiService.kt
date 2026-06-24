package com.ssafy.jjongle.tangram.data.remote

import com.ssafy.jjongle.tangram.data.remote.model.SingleGameDTO
import com.ssafy.jjongle.tangram.data.remote.model.TangramDetailDTO
import com.ssafy.jjongle.tangram.data.remote.model.TangramHistoriesPageDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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
