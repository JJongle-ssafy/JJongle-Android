package com.ssafy.jjongle.tangram.data.remote.model

import kotlinx.serialization.Serializable

/**
 * SingleGameDTO 외부 데이터 응답을 표현하는 DTO입니다.
 *
 * - 계층: tangram/data
 * - 책임: data 계층에서 외부 데이터 형태를 보존하고 domain/entity 모델로 변환합니다.
 */
@Serializable
data class SingleGameDTO(
    val stage: Int? = null
)

fun SingleGameDTO.toVO(): Int = stage ?: DEFAULT_STAGE_ID

private const val DEFAULT_STAGE_ID = 1
