package com.ssafy.jjongle.common.domain.repository

import com.ssafy.jjongle.common.entity.GameConnectionState
import com.ssafy.jjongle.common.entity.GameEvent
import com.ssafy.jjongle.common.entity.UserPosition
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface OXGameRepository {
    // 세션 관리
    fun saveSessionKey(sessionKey: String)
    fun getSessionKey(): String?
    fun isSessionValid(): Boolean
    fun clearSession()

    // Legacy 이름 유지: 현재 구현은 서버 WebSocket 대신 로컬 OX 게임 엔진을 시작/종료한다.
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

    // Legacy 이름 유지: 현재 구현은 서버 보고 대신 로컬 종료 처리를 수행한다.
    suspend fun finishOXGame(sessionKey: String)
}
