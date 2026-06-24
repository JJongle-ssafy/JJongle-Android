package com.ssafy.jjongle.oxgame.domain.usecase

import com.ssafy.jjongle.common.domain.base.BaseUseCase
import com.ssafy.jjongle.common.domain.helper.MessageHelper
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.helper.ResourceHelper
import com.ssafy.jjongle.tti.TTIHelper
import com.ssafy.jjongle.oxgame.domain.repository.OXGameRepository
import com.ssafy.jjongle.oxgame.entity.GameConnectionState
import com.ssafy.jjongle.oxgame.entity.GameEvent
import com.ssafy.jjongle.oxgame.entity.UserPosition
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * 게임 중 액션들을 처리하는 Use Case
 */
class GameActionUseCase @Inject constructor(
    private val oxGameRepository: OXGameRepository,
    resourceHelper: ResourceHelper,
    messageHelper: MessageHelper,
    navigationHelper: NavigationHelper,
    ttiHelper: TTIHelper,
) : BaseUseCase(resourceHelper, messageHelper, navigationHelper, ttiHelper) {

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
     * OX 게임 세션을 종료합니다.
     */
    fun endGameSession() {
        oxGameRepository.endGameSession()
    }


    /**
     * 최종 답변을 로컬 게임 엔진에 제출합니다.
     */
    suspend fun sendSubmitAnswer(
        sessionKey: String,
        quizId: Int,
        oAreaUserPositions: List<UserPosition>,
        xAreaUserPositions: List<UserPosition>
    ): Result<Unit> =
        executeWithCommonHttpHandling {
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
    suspend fun reportGameFinish(sessionKey: String): Result<Unit> =
        executeWithCommonHttpHandling { oxGameRepository.finishGameSession(sessionKey) }
}
