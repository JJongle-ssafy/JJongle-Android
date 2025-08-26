package com.ssafy.jjongle.data.remote.model

data class TangramHistoriesPageResponse(
    val content: List<TangramHistoryItemDto>
)

data class TangramHistoryItemDto(
    val stage: Int,
    val tangramId: Long,
    val animal: String
)

data class TangramDetailResponse(
    val story: String
)
