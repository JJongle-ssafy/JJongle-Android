package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName

data class BaseRequest<T>(
    @SerializedName("type") val type: String? = null,
    @SerializedName("data") val data: T? = null
)
