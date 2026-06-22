package com.ssafy.jjongle.domain.usecase

import com.ssafy.jjongle.domain.entity.GameConnectionState
import com.ssafy.jjongle.domain.entity.GameEvent
import com.ssafy.jjongle.domain.entity.UserPosition
import com.ssafy.jjongle.domain.repository.OXGameRepository
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * 게임 중 액션들을 처리하는 Use Case
 */
class GameActionUseCase @Inject constructor(
    private val oxGameRepository: OXGameRepository
) {

    /**
     * WebSocket 연결 상태를 관찰합니다.
     */
    val connectionState: StateFlow<GameConnectionState>
        get() = oxGameRepository.connectionState

    /**
     * 게임 이벤트를 관찰합니다.
     */
    val gameEvents: SharedFlow<GameEvent>
        get() = oxGameRepository.gameEvents
    

    /**
     * WebSocket 연결을 종료합니다.
     */
    fun disconnectWebSocket() {
        oxGameRepository.disconnectWebSocket()
    }


    /**
     * 최종 답변 제출 요청을 전송합니다.
     */
    fun sendSubmitAnswer(
        sessionKey: String,
        quizId: Int,
        oAreaUserPositions: List<UserPosition>,
        xAreaUserPositions: List<UserPosition>
    ) {
        oxGameRepository.sendSubmitAnswer(
            sessionKey = sessionKey,
            quizId = quizId,
            oAreaUserPositions = oAreaUserPositions,
            xAreaUserPositions = xAreaUserPositions
        )
    }

    /**
     * REST API로 게임 종료 보고
     */
    suspend fun reportGameFinish(sessionKey: String) {
        oxGameRepository.finishOXGame(sessionKey)
    }
}
