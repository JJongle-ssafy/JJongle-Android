package com.ssafy.jjongle.data.remote.model

import com.google.gson.annotations.SerializedName

@Deprecated("Legacy backend OX finish request retained during serverless renewal.")
data class FinishOXGameRequest(
    @SerializedName("sessionKey") val sessionKey: String? = null,
)
