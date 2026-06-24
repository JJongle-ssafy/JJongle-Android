package com.ssafy.jjongle.tangram.data.remote.model

import com.ssafy.jjongle.tangram.entity.TangramHistoriesPage
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TangramHistoriesPageDTO(
    val content: List<TangramHistoryItemDTO?>? = null,
    @SerialName("is_last")
    val isLast: Boolean? = null,
    val last: Boolean? = null,
)

fun TangramHistoriesPageDTO.toVO(): TangramHistoriesPage {
    val histories = content.orEmpty().mapNotNull { it?.toVO() }
    return TangramHistoriesPage(
        content = histories.toPersistentList(),
        isEnd = (isLast == true) || (last == true) || histories.isEmpty(),
    )
}
