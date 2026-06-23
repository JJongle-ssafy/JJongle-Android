package com.ssafy.jjongle.domain.entity

data class QuizResult(
    val quizId: Int,
    val correctAnswer: String,
    val correctCount: Int,
    val totalParticipants: Int,
    val correctUserIds: List<Int>
)