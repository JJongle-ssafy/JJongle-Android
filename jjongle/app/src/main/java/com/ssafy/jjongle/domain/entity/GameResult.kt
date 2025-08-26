package com.ssafy.jjongle.domain.entity

data class GameResult(
    val isCorrect: Boolean,
    val message: String,
    val score: Int? = null
) 