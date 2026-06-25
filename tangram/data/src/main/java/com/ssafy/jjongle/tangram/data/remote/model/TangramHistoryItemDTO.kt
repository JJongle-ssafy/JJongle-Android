package com.ssafy.jjongle.tangram.data.remote.model

import com.ssafy.jjongle.common.entity.AnimalType
import com.ssafy.jjongle.tangram.entity.TangramHistory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Tangram History Item 원격 응답의 필드 구조를 보존하는 DTO입니다.
 *
 * 서버 필드명, null 가능성, 페이징 형태를 data 계층 안에 가두고 repository에서 앱 내부 모델로 변환합니다.
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
