package com.ssafy.jjongle.oxgame.entity

import kotlinx.collections.immutable.ImmutableList

/**
 * Game Score는 OX 게임 흐름에서 계층 사이로 전달되는 도메인 값입니다.
 *
 * 원시 값 여러 개를 그대로 넘기지 않고 이름 있는 타입으로 묶어 호출 의도를 명확히 합니다.
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
