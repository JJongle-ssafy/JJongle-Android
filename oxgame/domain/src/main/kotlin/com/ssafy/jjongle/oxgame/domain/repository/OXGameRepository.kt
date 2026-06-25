package com.ssafy.jjongle.oxgame.domain.repository

import com.ssafy.jjongle.oxgame.entity.GameConnectionState
import com.ssafy.jjongle.oxgame.entity.GameEvent
import com.ssafy.jjongle.oxgame.entity.UserPosition
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * OXGame 기능이 domain 계층에서 기대하는 저장소 계약입니다.
 *
 * UseCase는 이 계약에만 의존하고, Firebase, Room, Retrofit 같은 실제 데이터 구현은 data 계층에 숨깁니다.
 */
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
