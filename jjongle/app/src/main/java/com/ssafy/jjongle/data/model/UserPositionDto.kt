package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName

/**
 * 서버와 통신하기 위한 사용자 위치 정보 DTO(Data Transfer Object)입니다.
 */
data class UserPositionDto(
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("x")
    val x: Double,
    @SerializedName("y")
    val y: Double
)
