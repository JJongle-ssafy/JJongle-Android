package com.ssafy.jjongle.data.remote.model

import com.ssafy.jjongle.data.model.orMissingServerId
import com.ssafy.jjongle.data.model.orMissingServerLongId
import com.ssafy.jjongle.domain.entity.AnimalType
import com.ssafy.jjongle.domain.entity.TangramHistory

data class TangramHistoryItemDto(
    val stage: Int? = null,
    val tangramId: Long? = null,
    val animal: String? = null
)

fun TangramHistoryItemDto.toDomain() = TangramHistory(
    stage = stage.orMissingServerId(),
    tangramId = tangramId.orMissingServerLongId(),
    animal = animal.toAnimalTypeOrFallback()
)

private fun String?.toAnimalTypeOrFallback(): AnimalType {
    return AnimalType.entries.firstOrNull { it.name.equals(this, ignoreCase = true) }
        ?: AnimalType.TURTLE
}
