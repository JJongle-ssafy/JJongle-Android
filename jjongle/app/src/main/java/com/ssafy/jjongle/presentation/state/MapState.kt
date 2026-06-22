package com.ssafy.jjongle.presentation.state

data class MapState(
    val characterX: Float = 371.875f,
    val characterY: Float = 678.9f,
    val isWalking: Boolean = false,
    val error: String? = null,
    val isBgmOn: Boolean = true
)
