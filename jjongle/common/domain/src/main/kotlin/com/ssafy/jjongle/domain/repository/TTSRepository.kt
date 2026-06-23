package com.ssafy.jjongle.domain.repository

import com.ssafy.jjongle.domain.entity.TtsAudio

interface TTSRepository {

    /**
     * 텍스트를 음성으로 변환
     * @param text 변환할 텍스트
     * @return 재생 가능한 오디오 바이트와 메타데이터
     */
    suspend fun generateTTS(
        text: String
    ): Result<TtsAudio>
}
