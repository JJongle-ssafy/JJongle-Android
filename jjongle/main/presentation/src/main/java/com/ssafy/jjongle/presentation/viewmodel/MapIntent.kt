package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.presentation.mvi.MviIntent

sealed interface MapIntent : MviIntent {
    data object StartWalking : MapIntent

    data class MoveCharacterTo(
        val x: Float,
        val y: Float,
    ) : MapIntent

    data object ToggleBgm : MapIntent
}
