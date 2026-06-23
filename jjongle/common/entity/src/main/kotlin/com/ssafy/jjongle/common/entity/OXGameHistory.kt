package com.ssafy.jjongle.common.entity

import java.time.LocalDateTime

data class OXGameHistory(
    val id: Long,
    val playedAt: LocalDateTime
)