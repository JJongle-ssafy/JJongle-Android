package com.ssafy.jjongle.data.websocket

import com.google.gson.Gson
import com.ssafy.jjongle.data.model.BaseResponse
import com.ssafy.jjongle.data.model.GameFinishResponse
import com.ssafy.jjongle.data.model.GameStartResponse
import com.ssafy.jjongle.data.model.SubmitResultResponse
import com.ssafy.jjongle.data.mapping.orMissingServerField
import com.ssafy.jjongle.data.mapping.orMissingServerId
import com.ssafy.jjongle.data.model.toDomainProfiles
import com.ssafy.jjongle.data.model.toDomain
import com.ssafy.jjongle.domain.entity.GameFinishEvent
import com.ssafy.jjongle.domain.entity.GameEvent
import com.ssafy.jjongle.domain.entity.GameStartEvent
import com.ssafy.jjongle.domain.entity.SubmitResultEvent
import com.ssafy.jjongle.domain.entity.UnknownGameEvent
import javax.inject.Inject

class GameWebSocketEventParser @Inject constructor(
    private val gson: Gson
) {
    fun parse(json: String): GameEvent {
        val baseResponse = gson.fromJson(json, BaseResponse::class.java)
            ?: return UnknownGameEvent

        return when (baseResponse.type) {
            "GAME_START" -> {
                val session = gson.fromJson(json, GameStartResponse::class.java)
                    ?.toDomain()
                    ?: GameStartResponse().toDomain()
                GameStartEvent(
                    quizzes = session.quizzes,
                    sessionKey = session.sessionKey
                )
            }

            "SUBMIT_RESULT" -> {
                val response = gson.fromJson(json, SubmitResultResponse::class.java)
                SubmitResultEvent(
                    quizId = response?.data?.quizId.orMissingServerId(),
                    correctAnswer = response?.data?.correctAnswer
                        .orMissingServerField("submitResult.correctAnswer"),
                    correctUserPositions = response?.data?.correctUserPositions
                        .orEmpty()
                        .mapNotNull { it?.toDomain() }
                )
            }

            "GAME_FINISH_RESULT" -> {
                val response = gson.fromJson(json, GameFinishResponse::class.java)
                GameFinishEvent(response?.toDomainProfiles().orEmpty())
            }

            else -> UnknownGameEvent
        }
    }
}
