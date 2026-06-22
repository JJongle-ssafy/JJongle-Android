package com.ssafy.jjongle.data.websocket

import com.google.gson.Gson
import com.ssafy.jjongle.data.model.AnalysisResultResponse
import com.ssafy.jjongle.data.model.BaseResponse
import com.ssafy.jjongle.data.model.GameFinishResponse
import com.ssafy.jjongle.data.model.GameStartResponse
import com.ssafy.jjongle.data.model.SubmitResultResponse
import com.ssafy.jjongle.data.model.toDomain
import com.ssafy.jjongle.domain.entity.GameEvent
import javax.inject.Inject

class GameWebSocketEventParser @Inject constructor(
    private val gson: Gson
) {
    fun parse(json: String): GameEvent {
        val baseResponse = gson.fromJson(json, BaseResponse::class.java)
        return when (baseResponse.type) {
            "GAME_START" -> {
                val response = gson.fromJson(json, GameStartResponse::class.java)
                GameEvent.GameStart(
                    quizzes = response.data.quizList.map { it.toDomain() },
                    sessionKey = response.data.sessionKey
                )
            }

            "SUBMIT_RESULT" -> {
                val response = gson.fromJson(json, SubmitResultResponse::class.java)
                GameEvent.SubmitResult(
                    quizId = response.data.quizId,
                    correctAnswer = response.data.correctAnswer,
                    correctUserPositions = response.data.correctUserPositions.map { it.toDomain() }
                )
            }

            "ANALYSIS_RESULT" -> {
                val response = gson.fromJson(json, AnalysisResultResponse::class.java)
                GameEvent.AnalysisResult(response.data)
            }

            "GAME_FINISH_RESULT" -> {
                val response = gson.fromJson(json, GameFinishResponse::class.java)
                GameEvent.GameFinish(response.data.userImages.map { it.toDomain() })
            }

            else -> GameEvent.Unknown
        }
    }
}
