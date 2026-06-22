package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName
import com.ssafy.jjongle.domain.entity.QuizSession

data class GameStartData(
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("quizList")
    val quizList: List<QuizResponse?>? = null,
    @SerializedName("sessionKey")
    val sessionKey: String? = null
)

fun GameStartData?.toDomain(): QuizSession {
    return QuizSession(
        quizzes = this?.quizList.orEmpty().mapNotNull { it?.toDomain() },
        sessionKey = this?.sessionKey.orMissingServerField("gameStart.sessionKey")
    )
}
