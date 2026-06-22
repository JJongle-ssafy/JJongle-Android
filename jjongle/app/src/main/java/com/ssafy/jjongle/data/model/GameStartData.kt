package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName

data class GameStartData(
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("quizList")
    val quizList: List<QuizResponse?>? = null,
    @SerializedName("sessionKey")
    val sessionKey: String? = null
)
