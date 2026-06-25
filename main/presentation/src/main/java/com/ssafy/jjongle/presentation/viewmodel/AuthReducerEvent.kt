package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.entity.AuthState
import com.ssafy.jjongle.common.presentation.mvi.ReducerEvent

/**
 * Auth Reducer Event는 메인 진행 중 발생한 도메인 이벤트입니다.
 *
 * 이벤트 종류를 타입으로 나눠 ViewModel이나 엔진이 문자열 분기 없이 게임 흐름을 처리하게 합니다.
 */
sealed interface AuthReducerEvent : ReducerEvent {
    data object LoadingStarted : AuthReducerEvent
    data class StateChanged(val authState: AuthState) : AuthReducerEvent
    data class Failed(val message: String) : AuthReducerEvent
}
