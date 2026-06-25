package com.ssafy.jjongle.tangram.data.remote.model

import com.ssafy.jjongle.common.entity.AnimalType
import com.ssafy.jjongle.tangram.entity.TangramHistory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * TangramHistoryItemDTO 외부 데이터 응답을 표현하는 DTO입니다.
 *
 * - 계층: tangram/data
 * - 책임: data 계층에서 외부 데이터 형태를 보존하고 domain/entity 모델로 변환합니다.
 */
@Serializable
data class TangramHistoryItemDTO(
    val stage: Int? = null,
    @SerialName("tangram_id")
    val tangramId: Long? = null,
    val animal: String? = null
)

fun TangramHistoryItemDTO.toVO() = TangramHistory(
    stage = stage ?: TangramHistory.MISSING_SERVER_ID,
    tangramId = tangramId ?: TangramHistory.MISSING_SERVER_LONG_ID,
    animal = animal.toAnimalTypeOrFallback()
)

private fun String?.toAnimalTypeOrFallback(): AnimalType {
    return AnimalType.entries.firstOrNull { it.name.equals(this, ignoreCase = true) }
        ?: AnimalType.TURTLE
}
