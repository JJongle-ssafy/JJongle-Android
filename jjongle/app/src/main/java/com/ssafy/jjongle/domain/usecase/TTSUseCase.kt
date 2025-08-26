package com.ssafy.jjongle.domain.usecase

import com.ssafy.jjongle.data.model.TTSResponse
import com.ssafy.jjongle.data.model.TTSResponseWrapper
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
     * @return TTSResponseWrapper
     */
    suspend fun generateTTS(
        text: String
    ): Result<TTSResponseWrapper> {
        return ttsRepository.generateTTS(text)
    }
    
    /**
     * 텍스트를 음성으로 변환 (기존 ResponseBody만 반환)
     * @param text 변환할 텍스트
     * @return TTSResponse
     */
    suspend fun generateTTSResponse(
        text: String
    ): Result<TTSResponse> {
        return ttsRepository.generateTTSResponse(text)
    }
}
