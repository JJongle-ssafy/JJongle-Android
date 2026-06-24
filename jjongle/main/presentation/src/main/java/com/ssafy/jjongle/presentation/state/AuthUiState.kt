package com.ssafy.jjongle.presentation.state

import com.ssafy.jjongle.common.entity.AuthState
import com.ssafy.jjongle.common.presentation.mvi.UiState

data class AuthUiState(
    val authState: AuthState = AuthState(isLoading = true),
) : UiState {
    companion object {
        val empty = AuthUiState()
    }
}
