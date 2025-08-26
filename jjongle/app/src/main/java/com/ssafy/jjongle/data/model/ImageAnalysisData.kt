package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName

data class ImageAnalysisData(
    @SerializedName("sessionKey") val sessionKey: String,
    @SerializedName("quizId") val quizId: Int,
    @SerializedName("imageBase64") val imageBase64: String
)

typealias SubmitAnswerData = ImageAnalysisData
typealias GameFinishData = ImageAnalysisData
