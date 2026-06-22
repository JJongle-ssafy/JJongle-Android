package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName

data class PositionSubmitData(
    @SerializedName("sessionKey")
    val sessionKey: String,
    @SerializedName("quizId")
    val quizId: Int,
    @SerializedName("oAreaUserPositions")
    val oAreaUserPositions: List<UserPositionDto>,
    @SerializedName("xAreaUserPositions")
    val xAreaUserPositions: List<UserPositionDto>
)
