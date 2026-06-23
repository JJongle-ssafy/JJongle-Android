package com.ssafy.jjongle.common.domain.usecase

import com.ssafy.jjongle.common.entity.GameConnectionState
import com.ssafy.jjongle.common.entity.GameEvent
import com.ssafy.jjongle.common.entity.UserPosition
import com.ssafy.jjongle.common.domain.repository.OXGameRepository
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
     * OX 게임 연결 상태를 관찰합니다.
     */
    val connectionState: StateFlow<GameConnectionState>
        get() = oxGameRepository.connectionState

    /**
     * 게임 이벤트를 관찰합니다.
     */
    val gameEvents: SharedFlow<GameEvent>
        get() = oxGameRepository.gameEvents
    

    /**
     * OX 게임 연결을 종료합니다.
     */
    fun disconnectWebSocket() {
        oxGameRepository.disconnectWebSocket()
    }


    /**
     * 최종 답변을 로컬 게임 엔진에 제출합니다.
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
     * 게임 종료 처리를 수행합니다.
     */
    suspend fun reportGameFinish(sessionKey: String) {
        oxGameRepository.finishOXGame(sessionKey)
    }
}
