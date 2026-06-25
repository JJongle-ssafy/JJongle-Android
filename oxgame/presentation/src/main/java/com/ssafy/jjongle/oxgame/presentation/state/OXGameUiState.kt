package com.ssafy.jjongle.oxgame.presentation.state

import com.ssafy.jjongle.oxgame.entity.GameConnectionState
import com.ssafy.jjongle.oxgame.entity.GameScore
import com.ssafy.jjongle.oxgame.entity.Quiz
import com.ssafy.jjongle.oxgame.entity.QuizResult
import com.ssafy.jjongle.oxgame.entity.QuizSession
import com.ssafy.jjongle.common.presentation.mvi.UiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

/**
 * OXGameUiState 화면이 구독하는 상태 모델입니다.
 *
 * - 계층: oxgame/presentation
 * - 책임: 렌더링에 필요한 값을 한곳에 모아 UI와 상태 변경 로직을 분리합니다.
 */
data class OXGameUiState(
    val connectionState: GameConnectionState = GameConnectionState.DISCONNECTED,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val gameState: GameState = GameState(),
    val quizSession: QuizSession? = null,
    val isQuizActive: Boolean = false,
    val timeLeft: Int = 10,
    val currentQuizIndex: Int = 0,
    val isAnswerSubmitted: Boolean = false,
    val showRewardAnimation: Boolean = false,
    val animationType: String? = null,
    val userPosition: Pair<Double, Double>? = null,
    val gameScore: GameScore = GameScore(0, 0, 0, persistentListOf()),
    val quizResults: ImmutableList<QuizResult> = persistentListOf(),
    val finalTop3: ImmutableList<Pair<Int, Int>> = persistentListOf(),
    val finishProfiles: ImmutableMap<Int, String> = persistentMapOf(),
) : UiState {
    val currentQuiz: Quiz?
        get() = quizSession?.quizzes?.getOrNull(currentQuizIndex)

    companion object {
        val empty = OXGameUiState()
    }
}
