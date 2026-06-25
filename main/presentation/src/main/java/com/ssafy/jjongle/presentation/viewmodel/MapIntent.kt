package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.presentation.mvi.MviIntent

/**
 * MapIntent 화면에서 ViewModel로 전달되는 사용자 입력을 정의합니다.
 *
 * - 계층: main/presentation
 * - 책임: UI 이벤트를 MVI intent로 분리해 상태 변경 진입점을 명확히 합니다.
 */
sealed interface MapIntent : MviIntent {
    data object StartWalking : MapIntent

    data class MoveCharacterTo(
        val x: Float,
        val y: Float,
    ) : MapIntent

    data object ToggleBgm : MapIntent
}
