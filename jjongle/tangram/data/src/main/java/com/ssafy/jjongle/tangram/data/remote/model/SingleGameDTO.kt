package com.ssafy.jjongle.tangram.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class SingleGameDTO(
    val stage: Int? = null
)

fun SingleGameDTO.toVO(): Int = stage ?: DEFAULT_STAGE_ID

private const val DEFAULT_STAGE_ID = 1
