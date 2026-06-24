package com.ssafy.jjongle.presentation.state

import com.ssafy.jjongle.common.presentation.mvi.UiState

data class MapState(
    val characterX: Float = 371.875f,
    val characterY: Float = 678.9f,
    val isWalking: Boolean = false,
    val error: String? = null,
    val isBgmOn: Boolean = true
) : UiState {
    companion object {
        val empty = MapState()
    }
}
