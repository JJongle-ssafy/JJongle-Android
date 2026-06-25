package com.ssafy.jjongle.tangram.data.remote.model

import com.ssafy.jjongle.tangram.entity.TangramHistoriesPage
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * TangramHistoriesPageDTO 외부 데이터 응답을 표현하는 DTO입니다.
 *
 * - 계층: tangram/data
 * - 책임: data 계층에서 외부 데이터 형태를 보존하고 domain/entity 모델로 변환합니다.
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
