package com.ssafy.jjongle.oxgame.entity

import kotlinx.collections.immutable.ImmutableList

data class QuizSession(
    val sessionKey: String,
    val quizzes: ImmutableList<Quiz>
)
