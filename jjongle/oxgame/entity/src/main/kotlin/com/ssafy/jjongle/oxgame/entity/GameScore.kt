package com.ssafy.jjongle.oxgame.entity

import kotlinx.collections.immutable.ImmutableList

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
