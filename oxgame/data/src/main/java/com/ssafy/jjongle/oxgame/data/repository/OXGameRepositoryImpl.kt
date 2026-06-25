package com.ssafy.jjongle.oxgame.data.repository

import com.ssafy.jjongle.oxgame.data.game.LocalOXGameEngine
import com.ssafy.jjongle.common.data.local.SessionDataSource
import com.ssafy.jjongle.oxgame.data.local.OXGameHistoryDao
import com.ssafy.jjongle.oxgame.data.local.OXGameHistoryEntity
import com.ssafy.jjongle.oxgame.data.local.toEntity
import com.ssafy.jjongle.oxgame.entity.GameConnectionState
import com.ssafy.jjongle.oxgame.entity.GameEvent
import com.ssafy.jjongle.oxgame.entity.UserPosition
import com.ssafy.jjongle.oxgame.domain.repository.OXGameRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * OXGame 저장소 계약을 data 계층에서 구현합니다.
 *
 * 원격/로컬 DataSource를 조합하고 DTO나 DB 모델을 앱 내부 모델로 변환해 domain 계층에 반환합니다.
 */
class OXGameRepositoryImpl @Inject constructor(
    private val sessionDataSource: SessionDataSource,
    private val localGameEngine: LocalOXGameEngine,
    private val historyDao: OXGameHistoryDao
) : OXGameRepository {

    private val _connectionState = MutableStateFlow(GameConnectionState.DISCONNECTED)
    private val _gameEvents = MutableSharedFlow<GameEvent>(replay = 1)

    override fun saveSessionKey(sessionKey: String) {
        sessionDataSource.saveSessionKey(sessionKey)
    }

    override fun getSessionKey(): String? = sessionDataSource.getSessionKey()

    override fun isSessionValid(): Boolean = sessionDataSource.isSessionValid()

    override fun clearSession() {
        sessionDataSource.clearSession()
        localGameEngine.clear()
        endGameSession()
    }

    override suspend fun startGameSession() {
        if (_connectionState.value == GameConnectionState.CONNECTED ||
            _connectionState.value == GameConnectionState.CONNECTING
        ) {
            return
        }

        _connectionState.value = GameConnectionState.CONNECTING
        var connected = false
        try {
            val event = localGameEngine.startGame()
            sessionDataSource.saveSessionKey(event.sessionKey)
            _connectionState.value = GameConnectionState.CONNECTED
            connected = true
            _gameEvents.emit(event)
        } finally {
            if (!connected && _connectionState.value == GameConnectionState.CONNECTING) {
                _connectionState.value = GameConnectionState.DISCONNECTED
            }
        }
    }

    override fun endGameSession() {
        if (_connectionState.value == GameConnectionState.CONNECTING) {
            _connectionState.value = GameConnectionState.DISCONNECTED
            return
        }
        _connectionState.value = GameConnectionState.DISCONNECTED
    }

    override val connectionState: StateFlow<GameConnectionState>
        get() = _connectionState.asStateFlow()

    override val gameEvents: SharedFlow<GameEvent>
        get() = _gameEvents.asSharedFlow()

    override suspend fun sendSubmitAnswer(
        sessionKey: String,
        quizId: Int,
        oAreaUserPositions: List<UserPosition>,
        xAreaUserPositions: List<UserPosition>
    ) {
        val event = localGameEngine.submitAnswer(
            sessionKey = sessionKey,
            quizId = quizId,
            oAreaUserPositions = oAreaUserPositions,
            xAreaUserPositions = xAreaUserPositions
        )
        _gameEvents.emit(event)
    }

    override suspend fun finishGameSession(sessionKey: String) {
        require(sessionKey.isNotBlank()) { "OX 게임 세션 키가 비어 있습니다." }
        val score = localGameEngine.buildGameScore()
        val historyId = historyDao.insertHistory(
            OXGameHistoryEntity(
                playedAtEpochMillis = System.currentTimeMillis(),
                totalQuizzes = score.totalQuizzes,
                completedQuizzes = score.completedQuizzes,
                totalCorrectAnswers = score.totalCorrectAnswers
            )
        )
        val notes = localGameEngine.buildWrongAnswerNotes()
            .map { it.toEntity(historyId) }
        if (notes.isNotEmpty()) {
            historyDao.insertWrongAnswerNotes(notes)
        }
    }
}
