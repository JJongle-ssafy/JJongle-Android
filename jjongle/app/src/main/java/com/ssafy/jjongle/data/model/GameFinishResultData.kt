package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName

data class GameFinishResultData(
    @SerializedName("userImages") val userImages: List<GameFinishProfile?>? = null
)
