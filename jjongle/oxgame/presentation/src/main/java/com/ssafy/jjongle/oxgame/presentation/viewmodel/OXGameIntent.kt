package com.ssafy.jjongle.oxgame.presentation.viewmodel

import com.ssafy.jjongle.common.presentation.mvi.MviIntent
import com.ssafy.jjongle.oxgame.presentation.vision.OXTrackedFace

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
