package com.ssafy.jjongle.oxgame.entity

import kotlinx.collections.immutable.ImmutableList

/**
 * GameScore 앱 내부에서 공유하는 도메인 값을 표현합니다.
 *
 * - 계층: oxgame/entity
 * - 책임: 불변 값과 도메인 의미를 계층 사이에 전달합니다.
 */
data class GameScore(
    val totalQuizzes: Int,
    val completedQuizzes: Int,
    val totalCorrectAnswers: Int,
    val quizResults: ImmutableList<QuizResult>
) {
    val averageCorrectRate: Float
        get() = if (completedQuizzes > 0) {
            totalCorrectAnswers.toFloat() / completedQuizzes
        } else 0f
}
