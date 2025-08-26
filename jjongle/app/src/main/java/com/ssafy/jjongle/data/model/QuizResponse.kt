package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName

data class QuizResponse(
    @SerializedName("quizId")
    val quizId: Int,

    @SerializedName("question")
    val question: String,

    @SerializedName("answer")
    val answer: String,

    @SerializedName("description")
    val description: String
)