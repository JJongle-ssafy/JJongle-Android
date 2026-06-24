package com.ssafy.jjongle.oxgame.entity

import kotlinx.collections.immutable.ImmutableList

data class GameStartEvent(
    val quizzes: ImmutableList<Quiz>,
    val sessionKey: String
) : GameEvent
