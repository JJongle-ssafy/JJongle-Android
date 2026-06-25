package com.ssafy.jjongle.oxgame.domain.usecase

import com.ssafy.jjongle.common.domain.error.HttpResponseException
import com.ssafy.jjongle.common.domain.error.HttpResponseStatus
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
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * GameAction의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class GameActionUseCaseTest {

    @Test
    fun report_game_finish_returns_success_when_repository_finishes() = runTest {
        val repository = FakeOXGameRepository()
        val useCase = useCase(repository)

        val result = useCase.reportGameFinish("session-1")

        assertTrue(result.isSuccess)
        assertEquals("session-1", repository.finishedSessionKey)
    }

    @Test
    fun report_game_finish_handles_common_http_error() = runTest {
        val messageHelper = RecordingMessageHelper()
        val exception = HttpResponseException(
            status = HttpResponseStatus.Unauthorized,
            rawCode = 401,
            errorRequestUrl = "https://example.test/ox/finish",
            msg = "Unauthorized",
        )
        val useCase = useCase(
            repository = FakeOXGameRepository(finishError = exception),
            messageHelper = messageHelper,
        )

        val result = useCase.reportGameFinish("session-1")

        assertTrue(result.isFailure)
        assertSame(exception, result.exceptionOrNull())
        assertEquals("세션이 만료되었습니다.", messageHelper.oneButtonDialogDesc)
    }

    private fun useCase(
        repository: OXGameRepository,
        messageHelper: MessageHelper = MessageHelper.NoOp,
    ): GameActionUseCase = GameActionUseCase(
        oxGameRepository = repository,
        resourceHelper = object : ResourceHelper {
            override fun getString(id: Int): String = id.toString()
        },
        messageHelper = messageHelper,
        navigationHelper = NavigationHelper.NoOp,
        ttiHelper = TTIHelper.NoOp,
    )

    private class FakeOXGameRepository(
        private val finishError: Throwable? = null,
    ) : OXGameRepository {
        var finishedSessionKey: String? = null

        override fun saveSessionKey(sessionKey: String) = Unit

        override fun getSessionKey(): String? = null

        override fun isSessionValid(): Boolean = true

        override fun clearSession() = Unit

        override suspend fun startGameSession() = Unit

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

        override suspend fun finishGameSession(sessionKey: String) {
            finishError?.let { throw it }
            finishedSessionKey = sessionKey
        }
    }

    private class RecordingMessageHelper : MessageHelper {
        var oneButtonDialogDesc: String? = null

        override fun showToast(messageText: String) = Unit

        override fun showSnackBar(messageText: String) = Unit

        override fun showOneButtonDialog(
            cantIgnore: Boolean,
            descText: String,
            onClickButton: () -> Unit,
        ) {
            oneButtonDialogDesc = descText
        }

        override fun showTwoButtonDialog(
            descText: String,
            onClickPositive: () -> Unit,
            onClickNegative: () -> Unit,
        ) = Unit
    }
}
