package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName

data class TTSRequest(
    @SerializedName("text")
    val text: String,
    
    @SerializedName("language")
    val language: String = "ko",
    
    @SerializedName("output_format")
    val outputFormat: String = "mp3"
)
