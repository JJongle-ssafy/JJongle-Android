package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName

/**
 * 최종 문제 결과 (SUBMIT_RESULT) 이벤트를 위한 데이터 모델입니다.
 */
data class SubmitResultResponse(
    @SerializedName("data")
    val data: SubmitResultData
) : BaseResponse("SUBMIT_RESULT")
