package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName

data class GameStartData(
    @SerializedName("message")
    val message: String,
    @SerializedName("quizList")
    val quizList: List<QuizResponse>,
    @SerializedName("sessionKey")
    val sessionKey: String
)
