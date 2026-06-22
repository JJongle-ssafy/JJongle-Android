package com.ssafy.jjongle.domain.entity

data class TtsAudio(
    val bytes: ByteArray,
    val audioLength: Double?,
    val contentType: String?
)
