package com.ssafy.jjongle.domain.usecase

import com.ssafy.jjongle.data.websocket.ConnectionState
import com.ssafy.jjongle.data.websocket.GameEvent
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
    val connectionState: StateFlow<ConnectionState>
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
     * 게임 세션을 완전히 종료합니다.
     */
    fun endGameSession() {
        oxGameRepository.clearSession()
    }

    /**
     * 이미지 분석 요청을 전송합니다.
     */
    fun sendImageAnalysis(sessionKey: String, quizId: Int, imageData: String) {
        oxGameRepository.sendImageAnalysis(sessionKey, quizId, imageData)
    }

    /**
     * 최종 답변 제출 요청을 전송합니다.
     */
    fun sendSubmitAnswer(sessionKey: String, quizId: Int, imageData: String) {
        oxGameRepository.sendSubmitAnswer(sessionKey, quizId, imageData)
    }

    /**
     * 게임 종료 결과 요청(GAME_FINISH)을 전송합니다.
     */
    fun sendGameFinish(sessionKey: String, quizId: Int, imageData: String) {
        oxGameRepository.sendGameFinish(sessionKey, quizId, imageData)
    }

    /**
     * REST API로 게임 종료 보고
     */
    suspend fun reportGameFinish(sessionKey: String) {
        oxGameRepository.finishOXGame(sessionKey)
    }
}