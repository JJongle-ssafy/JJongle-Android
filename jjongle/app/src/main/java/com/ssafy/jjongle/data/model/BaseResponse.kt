package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName

/**
 * 모든 WebSocket 응답의 기반이 되는 클래스입니다.
 * 'type' 필드를 공통으로 가집니다.
 */
open class BaseResponse(
    @SerializedName("type")
    open val type: String
)
