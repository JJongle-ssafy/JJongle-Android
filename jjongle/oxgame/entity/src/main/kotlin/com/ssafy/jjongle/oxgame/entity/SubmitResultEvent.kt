package com.ssafy.jjongle.oxgame.entity

import kotlinx.collections.immutable.ImmutableList

data class SubmitResultEvent(
    val quizId: Int,
    val correctAnswer: String,
    val correctUserPositions: ImmutableList<UserPosition>
) : GameEvent
