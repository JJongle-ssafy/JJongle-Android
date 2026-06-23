package com.ssafy.jjongle.domain.entity

data class SubmitResultEvent(
    val quizId: Int,
    val correctAnswer: String,
    val correctUserPositions: List<UserPosition>
) : GameEvent
