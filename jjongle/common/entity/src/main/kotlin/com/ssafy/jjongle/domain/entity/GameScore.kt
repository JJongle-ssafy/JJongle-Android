package com.ssafy.jjongle.domain.entity

data class GameScore(
    val totalQuizzes: Int,
    val completedQuizzes: Int,
    val totalCorrectAnswers: Int,
    val quizResults: List<QuizResult>
) {
    val averageCorrectRate: Float
        get() = if (completedQuizzes > 0) {
            totalCorrectAnswers.toFloat() / completedQuizzes
        } else 0f
}