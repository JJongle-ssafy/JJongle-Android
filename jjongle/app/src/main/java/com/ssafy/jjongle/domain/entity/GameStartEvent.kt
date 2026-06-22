package com.ssafy.jjongle.domain.entity

data class GameStartEvent(
    val quizzes: List<Quiz>,
    val sessionKey: String
) : GameEvent
