package com.ssafy.jjongle.common.domain.repository

import com.ssafy.jjongle.common.entity.OXGameHistory

// totalPages 등은 사용하지 않으므로 content만 보유
data class OXGameHistoryPage(
    val totalPages: Int,
    val content: List<OXGameHistory>
)