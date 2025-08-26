package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName

data class BaseRequest<T>(
    @SerializedName("type") val type: String,
    @SerializedName("data") val data: T
)