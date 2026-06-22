package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName
import com.ssafy.jjongle.domain.entity.QuizSession

/**
 * 게임 시작(GAME_START) 이벤트를 위한 데이터 모델입니다.
 */
data class GameStartResponse(
    @SerializedName("data")
    val data: GameStartData? = null
) : BaseResponse("GAME_START")

fun GameStartResponse.toDomain(): QuizSession {
    return data.toDomain()
}
