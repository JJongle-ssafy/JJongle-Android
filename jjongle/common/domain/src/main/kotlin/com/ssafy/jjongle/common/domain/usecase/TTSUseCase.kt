package com.ssafy.jjongle.common.domain.usecase

import com.ssafy.jjongle.common.entity.TtsAudio
import com.ssafy.jjongle.common.domain.repository.TTSRepository
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
        // TODO: TTS API 비용 절약을 위해 임시 비활성화. 아래 주석을 해제하면 복원됩니다.
        // return ttsRepository.generateTTS(text)
        return Result.failure(IllegalStateException("TTS 임시 비활성화"))
    }
}
