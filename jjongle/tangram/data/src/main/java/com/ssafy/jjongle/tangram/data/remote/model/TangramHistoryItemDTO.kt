package com.ssafy.jjongle.tangram.data.remote.model

import com.ssafy.jjongle.common.entity.AnimalType
import com.ssafy.jjongle.tangram.entity.TangramHistory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
