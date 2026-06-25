package com.ssafy.jjongle.presentation.state

import com.ssafy.jjongle.common.entity.AuthState
import com.ssafy.jjongle.common.presentation.mvi.UiState

/**
 * AuthUiState 화면이 구독하는 상태 모델입니다.
 *
 * - 계층: main/presentation
 * - 책임: 렌더링에 필요한 값을 한곳에 모아 UI와 상태 변경 로직을 분리합니다.
 */
data class AuthUiState(
    val authState: AuthState = AuthState(isLoading = true),
) : UiState {
    companion object {
        val empty = AuthUiState()
    }
}
