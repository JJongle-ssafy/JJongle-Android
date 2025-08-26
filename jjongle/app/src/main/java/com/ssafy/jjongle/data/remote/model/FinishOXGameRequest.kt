package com.ssafy.jjongle.data.remote.model

import com.google.gson.annotations.SerializedName

data class FinishOXGameRequest(
    @SerializedName("sessionKey") val sessionKey: String,
)


