package com.ssafy.jjongle.data.remote.mapper

import com.ssafy.jjongle.data.model.orMissingServerField
import com.ssafy.jjongle.data.model.orMissingServerId
import com.ssafy.jjongle.data.model.orMissingServerLongId
import com.ssafy.jjongle.data.remote.model.TangramDetailResponse
import com.ssafy.jjongle.data.remote.model.TangramHistoryItemDto
import com.ssafy.jjongle.domain.entity.AnimalType
import com.ssafy.jjongle.domain.entity.TangramDetail
import com.ssafy.jjongle.domain.entity.TangramHistory

fun TangramHistoryItemDto.toDomain() = TangramHistory(
    stage = stage.orMissingServerId(),
    tangramId = tangramId.orMissingServerLongId(),
    animal = animal.toAnimalTypeOrFallback()
)


fun TangramDetailResponse.toDomain(id: Long, type: AnimalType) = TangramDetail(
    tangramId = id,
    animal = type,
    story = story.orMissingServerField("tangram.story")
)

private fun String?.toAnimalTypeOrFallback(): AnimalType {
    return AnimalType.entries.firstOrNull { it.name.equals(this, ignoreCase = true) }
        ?: AnimalType.TURTLE
}
