package com.ssafy.jjongle.domain.repository

import com.ssafy.jjongle.data.websocket.ConnectionState
import com.ssafy.jjongle.data.websocket.GameEvent
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface OXGameRepository {
    // 세션 관리
    fun saveSessionKey(sessionKey: String)
    fun getSessionKey(): String?
    fun isSessionValid(): Boolean
    fun clearSession()

    // WebSocket 연결 관리
    fun connectWebSocket()
    fun disconnectWebSocket()
    val connectionState: StateFlow<ConnectionState>
    val gameEvents: SharedFlow<GameEvent>

    // WebSocket 메시지 전송
    fun sendImageAnalysis(sessionKey: String, quizId: Int, imageData: String)
    fun sendSubmitAnswer(sessionKey: String, quizId: Int, imageData: String)
    fun sendGameFinish(sessionKey: String, quizId: Int, imageData: String)

    // REST: 게임 종료 보고
    suspend fun finishOXGame(sessionKey: String)
}
