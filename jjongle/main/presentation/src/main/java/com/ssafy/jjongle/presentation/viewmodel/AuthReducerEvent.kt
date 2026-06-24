package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.entity.AuthState
import com.ssafy.jjongle.common.presentation.mvi.ReducerEvent

sealed interface AuthReducerEvent : ReducerEvent {
    data object LoadingStarted : AuthReducerEvent
    data class StateChanged(val authState: AuthState) : AuthReducerEvent
    data class Failed(val message: String) : AuthReducerEvent
}
