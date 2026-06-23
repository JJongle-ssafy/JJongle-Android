package com.ssafy.jjongle.common.entity

data class QuizSession(
    val sessionKey: String,
    val quizzes: List<Quiz>
)