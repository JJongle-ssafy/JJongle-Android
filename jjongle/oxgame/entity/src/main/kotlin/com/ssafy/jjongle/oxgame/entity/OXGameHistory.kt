package com.ssafy.jjongle.oxgame.entity

import java.time.LocalDateTime

data class OXGameHistory(
    val id: Long,
    val playedAt: LocalDateTime
)