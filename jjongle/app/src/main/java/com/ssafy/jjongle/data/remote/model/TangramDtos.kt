package com.ssafy.jjongle.data.remote.model

data class TangramHistoriesPageResponse(
    val content: List<TangramHistoryItemDto?>? = null
)

data class TangramHistoryItemDto(
    val stage: Int? = null,
    val tangramId: Long? = null,
    val animal: String? = null
)

data class TangramDetailResponse(
    val story: String? = null
)
