package com.ssafy.jjongle.domain.usecase

import com.ssafy.jjongle.domain.repository.OXGameRepository
import javax.inject.Inject

class StartOXGameUseCase @Inject constructor(
    private val oxGameRepository: OXGameRepository
) {
    /**
     * SharedPreferences에 저장된 세션 키로 WebSocket 연결을 시도합니다.
     */
    fun connectWebSocketWithSavedSession() {
        val sessionKey = oxGameRepository.getSessionKey()
        if (sessionKey != null) {
            oxGameRepository.connectWebSocket()
        }
        // 세션 키가 없는 경우는 ViewModel에서 처리 (예: 에러 메시지)
    }

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
     * 세션 키를 SharedPreferences에 저장합니다.
     */
    fun saveSessionKey(sessionKey: String) {
        oxGameRepository.saveSessionKey(sessionKey)
    }
}
