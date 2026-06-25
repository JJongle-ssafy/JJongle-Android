package com.ssafy.jjongle.oxgame.presentation.viewmodel

import com.ssafy.jjongle.oxgame.entity.GameConnectionState
import com.ssafy.jjongle.oxgame.entity.GameScore
import com.ssafy.jjongle.oxgame.entity.QuizResult
import com.ssafy.jjongle.oxgame.entity.QuizSession
import com.ssafy.jjongle.common.presentation.mvi.ReducerEvent
import com.ssafy.jjongle.oxgame.presentation.state.GameState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

/**
 * OXGame Reducer Event는 OX 게임 진행 중 발생한 도메인 이벤트입니다.
 *
 * 이벤트 종류를 타입으로 나눠 ViewModel이나 엔진이 문자열 분기 없이 게임 흐름을 처리하게 합니다.
 */
sealed interface OXGameReducerEvent : ReducerEvent {
    data class ConnectionStateChanged(val connectionState: GameConnectionState) : OXGameReducerEvent
    data object LoadingStarted : OXGameReducerEvent
    data class LoadingChanged(val isLoading: Boolean) : OXGameReducerEvent
    data class Failed(val message: String?) : OXGameReducerEvent
    data class ErrorMessageChanged(val message: String?) : OXGameReducerEvent
    data class GameStateChanged(val gameState: GameState) : OXGameReducerEvent
    data class QuizSessionChanged(val quizSession: QuizSession?) : OXGameReducerEvent
    data class QuizActiveChanged(val isActive: Boolean) : OXGameReducerEvent
    data class TimeLeftChanged(val timeLeft: Int) : OXGameReducerEvent
    data class CurrentQuizIndexChanged(val index: Int) : OXGameReducerEvent
    data class AnswerSubmittedChanged(val isSubmitted: Boolean) : OXGameReducerEvent
    data class RewardAnimationChanged(val isVisible: Boolean) : OXGameReducerEvent
    data class AnimationTypeChanged(val type: String?) : OXGameReducerEvent
    data class UserPositionChanged(val position: Pair<Double, Double>?) : OXGameReducerEvent
    data class GameScoreChanged(val gameScore: GameScore) : OXGameReducerEvent
    data class QuizResultsChanged(val quizResults: ImmutableList<QuizResult>) : OXGameReducerEvent
    data class FinalTop3Changed(val finalTop3: ImmutableList<Pair<Int, Int>>) : OXGameReducerEvent
    data class FinishProfilesChanged(val finishProfiles: ImmutableMap<Int, String>) : OXGameReducerEvent
}
