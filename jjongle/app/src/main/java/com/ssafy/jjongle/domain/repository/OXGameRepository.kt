package com.ssafy.jjongle.domain.repository

import com.ssafy.jjongle.domain.entity.GameConnectionState
import com.ssafy.jjongle.domain.entity.GameEvent
import com.ssafy.jjongle.domain.entity.UserPosition
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
    val connectionState: StateFlow<GameConnectionState>
    val gameEvents: SharedFlow<GameEvent>

    fun sendSubmitAnswer(
        sessionKey: String,
        quizId: Int,
        oAreaUserPositions: List<UserPosition>,
        xAreaUserPositions: List<UserPosition>
    )

    // REST: 게임 종료 보고
    suspend fun finishOXGame(sessionKey: String)
}
