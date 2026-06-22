package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName

data class PositionSubmitData(
    @SerializedName("sessionKey")
    val sessionKey: String? = null,
    @SerializedName("quizId")
    val quizId: Int? = null,
    @SerializedName("oAreaUserPositions")
    val oAreaUserPositions: List<UserPositionDto?>? = null,
    @SerializedName("xAreaUserPositions")
    val xAreaUserPositions: List<UserPositionDto?>? = null
)
