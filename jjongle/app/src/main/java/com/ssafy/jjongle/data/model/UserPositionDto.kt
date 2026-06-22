package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName
import com.ssafy.jjongle.domain.entity.UserPosition

/**
 * 서버와 통신하기 위한 사용자 위치 정보 DTO(Data Transfer Object)입니다.
 */
data class UserPositionDto(
    @SerializedName("userId")
    val userId: Int? = null,
    @SerializedName("x")
    val x: Double? = null,
    @SerializedName("y")
    val y: Double? = null
)

fun UserPositionDto.toDomain(): UserPosition {
    return UserPosition(
        userId = userId.orMissingServerId(),
        x = x.orMissingServerCoordinate(),
        y = y.orMissingServerCoordinate()
    )
}

fun UserPosition.toDto(): UserPositionDto {
    return UserPositionDto(
        userId = userId,
        x = x,
        y = y
    )
}
