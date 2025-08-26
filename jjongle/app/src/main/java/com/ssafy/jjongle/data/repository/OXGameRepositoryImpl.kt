package com.ssafy.jjongle.data.repository

import com.ssafy.jjongle.data.local.SessionDataSource
import com.ssafy.jjongle.data.remote.OXGameApiService
import com.ssafy.jjongle.data.websocket.ConnectionState
import com.ssafy.jjongle.data.websocket.GameEvent
import com.ssafy.jjongle.data.websocket.GameWebSocketManager
import com.ssafy.jjongle.domain.repository.OXGameRepository
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import com.ssafy.jjongle.data.remote.model.FinishOXGameRequest

class OXGameRepositoryImpl @Inject constructor(
    private val sessionDataSource: SessionDataSource,
    private val webSocketManager: GameWebSocketManager,
    private val oxGameApiService: OXGameApiService
) : OXGameRepository {

    // 세션 관리
    override fun saveSessionKey(sessionKey: String) {
        sessionDataSource.saveSessionKey(sessionKey)
    }

    override fun getSessionKey(): String? {
        return sessionDataSource.getSessionKey()
    }

    override fun isSessionValid(): Boolean {
        return sessionDataSource.isSessionValid()
    }

    override fun clearSession() {
        sessionDataSource.clearSession()
        disconnectWebSocket()
    }

    // WebSocket 연결 관리
    override fun connectWebSocket() {
        webSocketManager.connect()
    }

    override fun disconnectWebSocket() {
        webSocketManager.disconnect()
    }

    override val connectionState: StateFlow<ConnectionState>
        get() = webSocketManager.connectionState

    override val gameEvents: SharedFlow<GameEvent>
        get() = webSocketManager.gameEvents


    override fun sendImageAnalysis(sessionKey: String, quizId: Int, imageData: String) {
        webSocketManager.sendImageAnalysis(sessionKey, quizId, imageData)
    }

    override fun sendSubmitAnswer(sessionKey: String, quizId: Int, imageData: String) {
        webSocketManager.sendSubmitAnswer(sessionKey, quizId, imageData)
    }

    override fun sendGameFinish(sessionKey: String, quizId: Int, imageData: String) {
        webSocketManager.sendGameFinish(sessionKey, quizId, imageData)
    }

    override suspend fun finishOXGame(sessionKey: String) {
        val request = FinishOXGameRequest(sessionKey = sessionKey)
        oxGameApiService.finishOXGame(request)
    }
}
