package com.ssafy.jjongle.data.repository

import com.ssafy.jjongle.data.game.LocalOXGameEngine
import com.ssafy.jjongle.data.local.SessionDataSource
import com.ssafy.jjongle.data.local.oxgame.OXGameHistoryDao
import com.ssafy.jjongle.data.local.oxgame.OXGameHistoryEntity
import com.ssafy.jjongle.data.local.oxgame.toEntity
import com.ssafy.jjongle.domain.entity.GameConnectionState
import com.ssafy.jjongle.domain.entity.GameErrorEvent
import com.ssafy.jjongle.domain.entity.GameEvent
import com.ssafy.jjongle.domain.entity.UserPosition
import com.ssafy.jjongle.domain.repository.OXGameRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class OXGameRepositoryImpl @Inject constructor(
    private val sessionDataSource: SessionDataSource,
    private val localGameEngine: LocalOXGameEngine,
    private val historyDao: OXGameHistoryDao
) : OXGameRepository {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
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
        disconnectWebSocket()
    }

    override fun connectWebSocket() {
        if (_connectionState.value == GameConnectionState.CONNECTED ||
            _connectionState.value == GameConnectionState.CONNECTING
        ) {
            return
        }

        _connectionState.value = GameConnectionState.CONNECTING
        scope.launch {
            runCatching { localGameEngine.startGame() }
                .onSuccess { event ->
                    sessionDataSource.saveSessionKey(event.sessionKey)
                    _connectionState.value = GameConnectionState.CONNECTED
                    _gameEvents.emit(event)
                }
                .onFailure { error ->
                    _connectionState.value = GameConnectionState.ERROR
                    _gameEvents.emit(
                        GameErrorEvent(error.message ?: "OX 게임을 시작할 수 없습니다.")
                    )
                }
        }
    }

    override fun disconnectWebSocket() {
        _connectionState.value = GameConnectionState.DISCONNECTED
    }

    override val connectionState: StateFlow<GameConnectionState>
        get() = _connectionState.asStateFlow()

    override val gameEvents: SharedFlow<GameEvent>
        get() = _gameEvents.asSharedFlow()

    override fun sendSubmitAnswer(
        sessionKey: String,
        quizId: Int,
        oAreaUserPositions: List<UserPosition>,
        xAreaUserPositions: List<UserPosition>
    ) {
        scope.launch {
            runCatching {
                localGameEngine.submitAnswer(
                    sessionKey = sessionKey,
                    quizId = quizId,
                    oAreaUserPositions = oAreaUserPositions,
                    xAreaUserPositions = xAreaUserPositions
                )
            }.onSuccess { event ->
                _gameEvents.emit(event)
            }.onFailure { error ->
                _gameEvents.emit(
                    GameErrorEvent(error.message ?: "OX 정답을 제출할 수 없습니다.")
                )
            }
        }
    }

    override suspend fun finishOXGame(sessionKey: String) {
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
