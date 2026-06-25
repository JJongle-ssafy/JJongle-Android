package com.ssafy.jjongle.oxgame.presentation.viewmodel

import com.ssafy.jjongle.common.presentation.mvi.MviIntent
import com.ssafy.jjongle.oxgame.presentation.vision.OXTrackedFace

/**
 * OXGameIntent 화면에서 ViewModel로 전달되는 사용자 입력을 정의합니다.
 *
 * - 계층: oxgame/presentation
 * - 책임: UI 이벤트를 MVI intent로 분리해 상태 변경 진입점을 명확히 합니다.
 */
sealed interface OXGameIntent : MviIntent {
    data object EnterGame : OXGameIntent
    data object ConnectToGame : OXGameIntent
    data object StartCurrentQuiz : OXGameIntent
    data object ClearError : OXGameIntent
    data object RestartGame : OXGameIntent
    data object NextQuiz : OXGameIntent
    data object ShowExplanation : OXGameIntent
    data class UpdateTrackedFaces(val faces: List<OXTrackedFace>) : OXGameIntent
}
