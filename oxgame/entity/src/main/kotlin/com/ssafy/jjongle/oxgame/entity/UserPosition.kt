package com.ssafy.jjongle.oxgame.entity

/**
 * User Position는 OX 게임에서 사용하는 위치 정보를 담는 값입니다.
 *
 * 좌표를 Pair나 원시 숫자로 흩뿌리지 않고 의미 있는 도메인 값으로 전달합니다.
 */
data class UserPosition(
    val userId: Int,
    val x: Double,
    val y: Double
)
