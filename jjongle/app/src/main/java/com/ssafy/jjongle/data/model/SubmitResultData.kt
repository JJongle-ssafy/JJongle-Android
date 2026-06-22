package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName

data class SubmitResultData(
    @SerializedName("quizId")
    val quizId: Int? = null,
    @SerializedName("correctAnswer")
    val correctAnswer: String? = null,
    @SerializedName("correctUserPositions")
    val correctUserPositions: List<UserPositionDto?>? = null
)
