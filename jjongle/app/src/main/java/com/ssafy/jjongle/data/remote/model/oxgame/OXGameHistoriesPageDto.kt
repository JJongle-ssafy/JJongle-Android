package com.ssafy.jjongle.data.remote.model.oxgame

import com.ssafy.jjongle.common.domain.repository.OXGameHistoryPage

data class OXGameHistoriesPageDto(
    val totalPages: Int? = null,
    val content: List<OXGameHistoryDto?>? = null
)

fun OXGameHistoriesPageDto.toDomain(): OXGameHistoryPage {
    return OXGameHistoryPage(
        totalPages = totalPages ?: 0,
        content = content.orEmpty().mapNotNull { it?.toDomain() }
    )
}
