package com.ssafy.jjongle.tangram.data.remote.model

import com.ssafy.jjongle.common.entity.AnimalType
import com.ssafy.jjongle.tangram.entity.TangramDetail
import kotlinx.serialization.Serializable

@Serializable
data class TangramDetailDTO(
    val story: String? = null
)

fun TangramDetailDTO.toVO(id: Long, type: AnimalType) = TangramDetail(
    tangramId = id,
    animal = type,
    story = story ?: TangramDetail.MISSING_STORY
)
