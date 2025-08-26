package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName

data class SubmitResultData(
    @SerializedName("quizId")
    val quizId: Int,
    @SerializedName("correctAnswer")
    val correctAnswer: String,
    @SerializedName("correctUserPositions")
    val correctUserPositions: List<UserPositionDto>
)
