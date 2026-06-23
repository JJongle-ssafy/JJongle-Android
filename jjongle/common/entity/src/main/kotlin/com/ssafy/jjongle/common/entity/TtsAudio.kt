package com.ssafy.jjongle.common.entity

data class TtsAudio(
    val bytes: ByteArray,
    val audioLength: Double?,
    val contentType: String?
)
