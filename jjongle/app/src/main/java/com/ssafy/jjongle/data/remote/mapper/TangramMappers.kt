package com.ssafy.jjongle.data.remote.mapper

import com.ssafy.jjongle.data.remote.model.TangramDetailResponse
import com.ssafy.jjongle.data.remote.model.TangramHistoryItemDto
import com.ssafy.jjongle.domain.entity.AnimalType
import com.ssafy.jjongle.domain.entity.TangramDetail
import com.ssafy.jjongle.domain.entity.TangramHistory

fun TangramHistoryItemDto.toDomain() = TangramHistory(
    stage, tangramId, AnimalType.valueOf(animal)
)


fun TangramDetailResponse.toDomain(id: Long, type: AnimalType) = TangramDetail(
    tangramId = id,
    animal = type,
    story = story
)