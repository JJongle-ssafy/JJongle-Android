package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName

data class QuizResponse(
    @SerializedName("quizId")
    val quizId: Int? = null,

    @SerializedName("question")
    val question: String? = null,

    @SerializedName("answer")
    val answer: String? = null,

    @SerializedName("description")
    val description: String? = null
)
