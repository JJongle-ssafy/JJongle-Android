package com.ssafy.jjongle.tangram.data.remote.model

import com.ssafy.jjongle.tangram.entity.TangramHistoriesPage
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Tangram Histories Page 원격 응답의 필드 구조를 보존하는 DTO입니다.
 *
 * 서버 필드명, null 가능성, 페이징 형태를 data 계층 안에 가두고 repository에서 앱 내부 모델로 변환합니다.
 */
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
