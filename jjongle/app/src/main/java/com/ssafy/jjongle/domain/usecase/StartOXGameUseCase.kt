package com.ssafy.jjongle.domain.usecase

import com.ssafy.jjongle.domain.repository.OXGameRepository
import javax.inject.Inject

class StartOXGameUseCase @Inject constructor(
    private val oxGameRepository: OXGameRepository
) {
    /**
     * 주어진 세션 키로 WebSocket에 연결합니다.
     */
    fun connectWebSocket() {
        oxGameRepository.connectWebSocket()
    }

    /**
     * 현재 저장된 세션 키를 반환합니다.
     */
    fun getSessionKey(): String? {
        return oxGameRepository.getSessionKey()
    }

    /**
     * 세션 키를 저장합니다.
     */
    fun saveSessionKey(sessionKey: String) {
        oxGameRepository.saveSessionKey(sessionKey)
    }
}
