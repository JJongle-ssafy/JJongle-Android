package com.ssafy.jjongle.domain.repository

import com.ssafy.jjongle.domain.entity.OXGameHistory

// totalPages 등은 사용하지 않으므로 content만 보유
data class OXGameHistoryPage(
    val totalPages: Int,
    val content: List<OXGameHistory>
)