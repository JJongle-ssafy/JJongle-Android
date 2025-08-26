package com.ssafy.jjongle.domain.entity

data class QuizSession(
    val sessionKey: String,
    val quizzes: List<Quiz>
)