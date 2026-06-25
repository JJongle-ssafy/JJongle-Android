package com.ssafy.jjongle.oxgame.presentation.viewmodel

import com.ssafy.jjongle.common.presentation.mvi.MviIntent
import com.ssafy.jjongle.oxgame.presentation.vision.OXTrackedFace

/**
 * OX 게임 화면에서 ViewModel로 전달되는 사용자 입력과 화면 이벤트입니다.
 *
 * 버튼 클릭, 화면 진입, 선택 변경 같은 입력을 타입으로 분리해 상태 변경의 시작점을 명확히 남깁니다.
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
