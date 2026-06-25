package com.ssafy.jjongle.tangram.data.remote.model

import kotlinx.serialization.Serializable

/**
 * Single Game 원격 응답의 필드 구조를 보존하는 DTO입니다.
 *
 * 서버 필드명, null 가능성, 페이징 형태를 data 계층 안에 가두고 repository에서 앱 내부 모델로 변환합니다.
 */
@Serializable
data class SingleGameDTO(
    val stage: Int? = null
)

fun SingleGameDTO.toVO(): Int = stage ?: DEFAULT_STAGE_ID

private const val DEFAULT_STAGE_ID = 1
