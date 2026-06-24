package com.ssafy.jjongle.oxgame.entity

import kotlinx.collections.immutable.ImmutableList

data class QuizResult(
    val quizId: Int,
    val correctAnswer: String,
    val correctCount: Int,
    val totalParticipants: Int,
    val correctUserIds: ImmutableList<Int>
)
