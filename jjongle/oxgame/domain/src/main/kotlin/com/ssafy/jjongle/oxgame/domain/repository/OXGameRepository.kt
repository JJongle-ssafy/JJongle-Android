package com.ssafy.jjongle.oxgame.domain.repository

import com.ssafy.jjongle.oxgame.entity.GameConnectionState
import com.ssafy.jjongle.oxgame.entity.GameEvent
import com.ssafy.jjongle.oxgame.entity.UserPosition
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface OXGameRepository {
    // 세션 관리
    fun saveSessionKey(sessionKey: String)
    fun getSessionKey(): String?
    fun isSessionValid(): Boolean
    fun clearSession()

    suspend fun startGameSession()
    fun endGameSession()
    val connectionState: StateFlow<GameConnectionState>
    val gameEvents: SharedFlow<GameEvent>

    suspend fun sendSubmitAnswer(
        sessionKey: String,
        quizId: Int,
        oAreaUserPositions: List<UserPosition>,
        xAreaUserPositions: List<UserPosition>
    )

    suspend fun finishGameSession(sessionKey: String)
}
