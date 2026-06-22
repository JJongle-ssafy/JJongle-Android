package com.ssafy.jjongle.data.remote.model

import com.ssafy.jjongle.data.mapping.orMissingServerField
import com.ssafy.jjongle.domain.entity.AnimalType
import com.ssafy.jjongle.domain.entity.TangramDetail

data class TangramDetailResponse(
    val story: String? = null
)

fun TangramDetailResponse.toDomain(id: Long, type: AnimalType) = TangramDetail(
    tangramId = id,
    animal = type,
    story = story.orMissingServerField("tangram.story")
)
