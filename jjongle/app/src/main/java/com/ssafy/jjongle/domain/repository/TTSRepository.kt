package com.ssafy.jjongle.domain.repository

import com.ssafy.jjongle.data.model.TTSResponse
import com.ssafy.jjongle.data.model.TTSResponseWrapper

interface TTSRepository {
    
    /**
     * 텍스트를 음성으로 변환
     * @param text 변환할 텍스트
     * @return TTSResponseWrapper (ResponseBody + 오디오 길이)
     */
    suspend fun generateTTS(
        text: String
    ): Result<TTSResponseWrapper>
    
    /**
     * 텍스트를 음성으로 변환 (기존 ResponseBody만 반환)
     * @param text 변환할 텍스트
     * @return TTSResponse (ResponseBody)
     */
    suspend fun generateTTSResponse(
        text: String
    ): Result<TTSResponse>
}
