package com.ssafy.jjongle.domain.usecase

import com.ssafy.jjongle.domain.entity.TtsAudio
import com.ssafy.jjongle.domain.repository.TTSRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TTSUseCase @Inject constructor(
    private val ttsRepository: TTSRepository
) {
    
    /**
     * 텍스트를 음성으로 변환 (오디오 길이 정보 포함)
     * @param text 변환할 텍스트
     */
    suspend fun generateTTS(
        text: String
    ): Result<TtsAudio> {
        return ttsRepository.generateTTS(text)
    }
}
