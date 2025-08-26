package com.ssafy.jjongle.data.remote

import com.ssafy.jjongle.data.remote.model.oxgame.OXGameHistoriesPageDto
import com.ssafy.jjongle.data.remote.model.oxgame.OXGameWrongAnswerNoteDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

import com.ssafy.jjongle.data.remote.model.FinishOXGameRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface OXGameApiService {

    @GET("/group-game/histories")
    suspend fun getHistories(
//        @Header("X-User-Id") userId: Long,
        @Query("page") page: Int
    ): OXGameHistoriesPageDto

    @GET("/group-game/history/{historyId}")
    suspend fun getHistoryDetail(
        @Path("historyId") historyId: Long
    ): List<OXGameWrongAnswerNoteDto>
    @POST("/group-game/finish")
    suspend fun finishOXGame(
        @Body request: FinishOXGameRequest
    ): Unit

}
