package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.presentation.mvi.ReducerEvent

sealed interface MapReducerEvent : ReducerEvent {
    data object WalkingStarted : MapReducerEvent

    data class CharacterMoved(
        val x: Float,
        val y: Float,
    ) : MapReducerEvent

    data class BgmChanged(
        val enabled: Boolean,
    ) : MapReducerEvent
}
