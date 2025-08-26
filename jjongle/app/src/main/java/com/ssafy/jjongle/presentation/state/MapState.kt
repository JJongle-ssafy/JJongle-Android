package com.ssafy.jjongle.presentation.state

data class MapState(
    val characterX: Float = 170f,
    val characterY: Float = 310f,
    val isWalking: Boolean = false,
    val error: String? = null,
    val isBgmOn: Boolean = true
)