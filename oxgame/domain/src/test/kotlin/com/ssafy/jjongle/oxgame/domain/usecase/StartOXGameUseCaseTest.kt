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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Start OXGame Use Case Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class StartOXGameUseCaseTest {

    @Test
    fun start_ox_game_use_case_uses_base_use_case_contract() {
        assertTrue(BaseUseCase::class.java.isAssignableFrom(StartOXGameUseCase::class.java))
    }

    @Test
    fun session_methods_delegate_to_repository() = runTest {
        val repository = FakeOXGameRepository()
        val useCase = useCase(repository)

        val result = useCase.startGameSession()
        useCase.saveSessionKey("session-1")

        assertTrue(result.isSuccess)
        assertEquals(1, repository.startGameSessionCallCount)
        assertEquals("session-1", useCase.getSessionKey())
    }

    private fun useCase(repository: OXGameRepository): StartOXGameUseCase = StartOXGameUseCase(
        oxGameRepository = repository,
        resourceHelper = object : ResourceHelper {
            override fun getString(id: Int): String = id.toString()
        },
        messageHelper = MessageHelper.NoOp,
        navigationHelper = NavigationHelper.NoOp,
        ttiHelper = TTIHelper.NoOp,
    )

    private class FakeOXGameRepository : OXGameRepository {
        var startGameSessionCallCount: Int = 0
        private var sessionKey: String? = null

        override fun saveSessionKey(sessionKey: String) {
            this.sessionKey = sessionKey
        }

        override fun getSessionKey(): String? = sessionKey

        override fun isSessionValid(): Boolean = sessionKey != null

        override fun clearSession() {
            sessionKey = null
        }

        override suspend fun startGameSession() {
            startGameSessionCallCount += 1
        }

        override fun endGameSession() = Unit

        override val connectionState: StateFlow<GameConnectionState> =
            MutableStateFlow(GameConnectionState.DISCONNECTED)

        override val gameEvents: SharedFlow<GameEvent> = MutableSharedFlow()

        override suspend fun sendSubmitAnswer(
            sessionKey: String,
            quizId: Int,
            oAreaUserPositions: List<UserPosition>,
            xAreaUserPositions: List<UserPosition>,
        ) = Unit

        override suspend fun finishGameSession(sessionKey: String) = Unit
    }
}
