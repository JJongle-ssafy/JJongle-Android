package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName

data class AnalysisResultData(
    // 이미지에서 인식된 사용자들의 위치 정보 목록
    @SerializedName("quizId")
    val quizId: Int,
    @SerializedName("message")
    val message: String,
)
