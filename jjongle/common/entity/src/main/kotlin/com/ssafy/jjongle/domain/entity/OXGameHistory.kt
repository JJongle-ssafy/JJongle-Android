package com.ssafy.jjongle.domain.entity

import java.time.LocalDateTime

data class OXGameHistory(
    val id: Long,
    val playedAt: LocalDateTime
)