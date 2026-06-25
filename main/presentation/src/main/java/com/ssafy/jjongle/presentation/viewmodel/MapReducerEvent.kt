package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.presentation.mvi.ReducerEvent

/**
 * MapReducerEvent ViewModel 내부 상태 변경 이벤트를 정의합니다.
 *
 * - 계층: main/presentation
 * - 책임: 비동기 결과와 사용자 입력을 reducer가 처리할 수 있는 이벤트로 정리합니다.
 */
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
