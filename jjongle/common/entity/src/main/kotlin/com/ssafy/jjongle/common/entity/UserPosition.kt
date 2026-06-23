package com.ssafy.jjongle.common.entity

/**
 * 사용자 위치 정보를 나타내는 도메인 엔티티입니다.
 */
data class UserPosition(
    val userId: Int,
    val x: Double,
    val y: Double
)
