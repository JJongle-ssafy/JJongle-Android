package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.presentation.mvi.ReducerEvent

/**
 * Map Reducer Event는 메인 진행 중 발생한 도메인 이벤트입니다.
 *
 * 이벤트 종류를 타입으로 나눠 ViewModel이나 엔진이 문자열 분기 없이 게임 흐름을 처리하게 합니다.
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
