package com.ssafy.jjongle.data.model

import okhttp3.ResponseBody

// SuperTone API는 오디오 스트림을 직접 반환하므로 ResponseBody를 사용
typealias TTSResponse = ResponseBody

// TTS 응답 래퍼 클래스 (오디오 길이 정보 포함)
data class TTSResponseWrapper(
    val responseBody: ResponseBody,
    val audioLength: Double?,
    val audioBytes: ByteArray? = null
)
