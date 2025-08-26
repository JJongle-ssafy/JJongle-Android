package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName

data class AnalysisResultResponse(
    @SerializedName("data")
    val data: String
) : BaseResponse("ANALYSIS_RESULT")