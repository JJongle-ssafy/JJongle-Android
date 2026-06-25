package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.entity.AuthState
import com.ssafy.jjongle.common.presentation.mvi.ReducerEvent

/**
 * AuthReducerEvent ViewModel 내부 상태 변경 이벤트를 정의합니다.
 *
 * - 계층: main/presentation
 * - 책임: 비동기 결과와 사용자 입력을 reducer가 처리할 수 있는 이벤트로 정리합니다.
 */
sealed interface AuthReducerEvent : ReducerEvent {
    data object LoadingStarted : AuthReducerEvent
    data class StateChanged(val authState: AuthState) : AuthReducerEvent
    data class Failed(val message: String) : AuthReducerEvent
}
