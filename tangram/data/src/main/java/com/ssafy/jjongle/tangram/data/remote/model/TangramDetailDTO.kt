package com.ssafy.jjongle.tangram.data.remote.model

import com.ssafy.jjongle.common.entity.AnimalType
import com.ssafy.jjongle.tangram.entity.TangramDetail
import kotlinx.serialization.Serializable

/**
 * TangramDetailDTO 외부 데이터 응답을 표현하는 DTO입니다.
 *
 * - 계층: tangram/data
 * - 책임: data 계층에서 외부 데이터 형태를 보존하고 domain/entity 모델로 변환합니다.
 */
@Serializable
data class TangramDetailDTO(
    val story: String? = null
)

fun TangramDetailDTO.toVO(id: Long, type: AnimalType) = TangramDetail(
    tangramId = id,
    animal = type,
    story = story ?: TangramDetail.MISSING_STORY
)
