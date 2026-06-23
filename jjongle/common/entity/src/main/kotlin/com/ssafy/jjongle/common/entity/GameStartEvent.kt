package com.ssafy.jjongle.common.entity

data class GameStartEvent(
    val quizzes: List<Quiz>,
    val sessionKey: String
) : GameEvent
