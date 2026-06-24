package com.ssafy.jjongle.oxgame.presentation.viewmodel

import com.ssafy.jjongle.common.domain.helper.MessageHelper
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.helper.ResourceHelper
import com.ssafy.jjongle.tti.TTIHelper
import com.ssafy.jjongle.oxgame.domain.repository.OXGameRepository
import com.ssafy.jjongle.oxgame.domain.usecase.CalculateOXRankingsUseCase
import com.ssafy.jjongle.oxgame.domain.usecase.GameActionUseCase
import com.ssafy.jjongle.oxgame.domain.usecase.StartOXGameUseCase
import com.ssafy.jjongle.oxgame.domain.usecase.UpdateOXScoreUseCase
import com.ssafy.jjongle.oxgame.entity.GameConnectionState
import com.ssafy.jjongle.oxgame.entity.GameErrorEvent
import com.ssafy.jjongle.oxgame.entity.GameEvent
import com.ssafy.jjongle.oxgame.entity.GameFinishEvent
import com.ssafy.jjongle.oxgame.entity.GameProfileImage
import com.ssafy.jjongle.oxgame.entity.GameStartEvent
import com.ssafy.jjongle.oxgame.entity.Quiz
import com.ssafy.jjongle.oxgame.entity.SubmitResultEvent
import com.ssafy.jjongle.oxgame.entity.UserPosition
import com.ssafy.jjongle.oxgame.presentation.vision.OXAnswerArea
import com.ssafy.jjongle.oxgame.presentation.vision.OXTrackedFace
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OXGameViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun connect_to_game_can_be_requested_through_mvi_intent() = runTest {
        val repository = FakeOXGameRepository()
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(GameConnectionState.DISCONNECTED, viewModel.uiState.value.connectionState)

        viewModel.onIntent(OXGameIntent.ConnectToGame)
        advanceUntilIdle()

        assertTrue(repository.startGameSessionCalled)
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun connection_state_is_reduced_into_mvi_ui_state() = runTest {
        val repository = FakeOXGameRepository()
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        repository.connectionState.value = GameConnectionState.CONNECTING
        advanceUntilIdle()

        assertEquals(GameConnectionState.CONNECTING, viewModel.uiState.value.connectionState)
    }

    @Test
    fun game_start_event_clears_loading_in_mvi_ui_state() = runTest {
        val repository = FakeOXGameRepository()
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.onIntent(OXGameIntent.ConnectToGame)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isLoading)

        repository.emit(
            GameStartEvent(
                sessionKey = "session-key",
                quizzes = listOf(
                    Quiz(
                        id = 1,
                        question = "question",
                        answer = "O",
                        description = "description",
                    ),
                ).toPersistentList(),
            ),
        )
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("session-key", viewModel.uiState.value.quizSession?.sessionKey)
        assertEquals(1, viewModel.uiState.value.currentQuiz?.id)
    }

    @Test
    fun enter_game_intent_starts_game_session_through_mvi_entrypoint() = runTest {
        val repository = FakeOXGameRepository()
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.onIntent(OXGameIntent.EnterGame)
        advanceUntilIdle()

        assertTrue(repository.startGameSessionCalled)
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun start_current_quiz_can_be_requested_through_mvi_intent() = runTest {
        val repository = FakeOXGameRepository()
        val viewModel = createViewModel(repository)
        advanceUntilIdle()
        repository.emit(
            GameStartEvent(
                sessionKey = "session-key",
                quizzes = listOf(
                    Quiz(
                        id = 1,
                        question = "question",
                        answer = "O",
                        description = "description",
                    ),
                ).toPersistentList(),
            ),
        )
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isQuizActive)

        viewModel.onIntent(OXGameIntent.StartCurrentQuiz)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.gameState.isGameActive)
        assertTrue(viewModel.uiState.value.isQuizActive)
        assertTrue(viewModel.uiState.value.timeLeft in 0..10)
    }

    @Test
    fun clear_error_can_be_requested_through_mvi_intent() = runTest {
        val repository = FakeOXGameRepository()
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        repository.emit(GameErrorEvent("network failed"))
        advanceUntilIdle()
        assertEquals("network failed", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.gameState.isGameActive)

        viewModel.onIntent(OXGameIntent.ClearError)
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun restart_game_can_be_requested_through_mvi_intent() = runTest {
        val repository = FakeOXGameRepository()
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.onIntent(OXGameIntent.RestartGame)
        advanceUntilIdle()

        assertTrue(repository.endGameSessionCalled)
        assertTrue(repository.startGameSessionCalled)
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun next_quiz_can_be_requested_through_mvi_intent() = runTest {
        val repository = FakeOXGameRepository()
        val viewModel = createViewModel(repository)
        advanceUntilIdle()
        repository.emit(
            GameStartEvent(
                sessionKey = "session-key",
                quizzes = listOf(
                    Quiz(
                        id = 1,
                        question = "question-1",
                        answer = "O",
                        description = "description-1",
                    ),
                    Quiz(
                        id = 2,
                        question = "question-2",
                        answer = "X",
                        description = "description-2",
                    ),
                ).toPersistentList(),
            ),
        )
        advanceUntilIdle()
        repository.emit(
            SubmitResultEvent(
                quizId = 1,
                correctAnswer = "O",
                correctUserPositions = listOf(
                    UserPosition(userId = 1, x = 0.25, y = 0.75),
                ).toPersistentList(),
            ),
        )
        advanceUntilIdle()
        assertEquals(0, viewModel.uiState.value.currentQuizIndex)
        assertTrue(viewModel.uiState.value.isAnswerSubmitted)
        assertTrue(viewModel.uiState.value.showRewardAnimation)
        assertEquals("O", viewModel.uiState.value.animationType)
        assertEquals(Pair(0.25, 0.75), viewModel.uiState.value.userPosition)
        assertEquals(2, viewModel.uiState.value.gameScore.totalQuizzes)
        assertEquals(1, viewModel.uiState.value.gameScore.completedQuizzes)
        assertEquals(1, viewModel.uiState.value.gameScore.totalCorrectAnswers)
        assertEquals(1, viewModel.uiState.value.quizResults.size)

        viewModel.onIntent(OXGameIntent.NextQuiz)
        advanceUntilIdle()
        assertEquals(1, viewModel.uiState.value.currentQuizIndex)
        assertEquals(2, viewModel.uiState.value.currentQuiz?.id)
        assertFalse(viewModel.uiState.value.isAnswerSubmitted)
    }

    @Test
    fun show_explanation_can_be_requested_through_mvi_intent() = runTest {
        val repository = FakeOXGameRepository()
        val viewModel = createViewModel(repository)
        advanceUntilIdle()
        repository.emit(
            GameStartEvent(
                sessionKey = "session-key",
                quizzes = listOf(
                    Quiz(
                        id = 1,
                        question = "question",
                        answer = "O",
                        description = "description",
                    ),
                ).toPersistentList(),
            ),
        )
        advanceUntilIdle()
        viewModel.onIntent(OXGameIntent.StartCurrentQuiz)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isQuizActive)

        viewModel.onIntent(OXGameIntent.ShowExplanation)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isQuizActive)
        assertFalse(viewModel.uiState.value.showRewardAnimation)
    }

    @Test
    fun tracked_faces_can_be_updated_through_mvi_intent() = runTest {
        val repository = FakeOXGameRepository()
        val viewModel = createViewModel(repository)
        advanceUntilIdle()
        repository.emit(
            GameStartEvent(
                sessionKey = "session-key",
                quizzes = listOf(
                    Quiz(
                        id = 1,
                        question = "question",
                        answer = "O",
                        description = "description",
                    ),
                ).toPersistentList(),
            ),
        )
        advanceUntilIdle()
        viewModel.onIntent(OXGameIntent.StartCurrentQuiz)
        advanceUntilIdle()
        val faces = listOf(
            OXTrackedFace(
                participantId = 7,
                x = 0.25,
                y = 0.5,
                area = OXAnswerArea.O,
                profileImageBase64 = "profile",
            ),
        )

        viewModel.onIntent(OXGameIntent.UpdateTrackedFaces(faces))
        advanceUntilIdle()

        assertEquals("profile", viewModel.uiState.value.finishProfiles[7])
    }

    @Test
    fun game_finish_event_updates_mvi_final_results_state() = runTest {
        val repository = FakeOXGameRepository()
        val viewModel = createViewModel(repository)
        advanceUntilIdle()
        repository.emit(
            GameStartEvent(
                sessionKey = "session-key",
                quizzes = listOf(
                    Quiz(
                        id = 1,
                        question = "question",
                        answer = "O",
                        description = "description",
                    ),
                ).toPersistentList(),
            ),
        )
        advanceUntilIdle()
        repository.emit(
            SubmitResultEvent(
                quizId = 1,
                correctAnswer = "O",
                correctUserPositions = listOf(
                    UserPosition(userId = 7, x = 0.25, y = 0.75),
                ).toPersistentList(),
            ),
        )
        advanceUntilIdle()

        repository.emit(
            GameFinishEvent(
                profiles = listOf(
                    GameProfileImage(userId = 7, base64 = "profile"),
                ).toPersistentList(),
            ),
        )
        advanceUntilIdle()
        assertEquals(listOf(7 to 1), viewModel.uiState.value.finalTop3)
        assertEquals("profile", viewModel.uiState.value.finishProfiles[7])
        assertTrue(viewModel.uiState.value.gameState.isGameFinished)
    }

    private fun createViewModel(repository: FakeOXGameRepository): OXGameViewModel {
        val resourceHelper = object : ResourceHelper {
            override fun getString(id: Int): String = id.toString()
        }
        val startUseCase = StartOXGameUseCase(
            oxGameRepository = repository,
            resourceHelper = resourceHelper,
            messageHelper = MessageHelper.NoOp,
            navigationHelper = NavigationHelper.NoOp,
            ttiHelper = TTIHelper.NoOp,
        )
        val actionUseCase = GameActionUseCase(
            oxGameRepository = repository,
            resourceHelper = resourceHelper,
            messageHelper = MessageHelper.NoOp,
            navigationHelper = NavigationHelper.NoOp,
            ttiHelper = TTIHelper.NoOp,
        )
        return OXGameViewModel(
            startGameUseCase = startUseCase,
            gameActionUseCase = actionUseCase,
            updateOXScoreUseCase = UpdateOXScoreUseCase(),
            calculateOXRankingsUseCase = CalculateOXRankingsUseCase(),
        )
    }

    private class FakeOXGameRepository : OXGameRepository {
        override val connectionState = MutableStateFlow(GameConnectionState.DISCONNECTED)
        override val gameEvents = MutableSharedFlow<GameEvent>()
        var startGameSessionCalled: Boolean = false
            private set
        var endGameSessionCalled: Boolean = false
            private set
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
            startGameSessionCalled = true
        }

        suspend fun emit(event: GameEvent) {
            gameEvents.emit(event)
        }

        override fun endGameSession() {
            endGameSessionCalled = true
        }

        override suspend fun sendSubmitAnswer(
            sessionKey: String,
            quizId: Int,
            oAreaUserPositions: List<com.ssafy.jjongle.oxgame.entity.UserPosition>,
            xAreaUserPositions: List<com.ssafy.jjongle.oxgame.entity.UserPosition>
        ) = Unit

        override suspend fun finishGameSession(sessionKey: String) = Unit
    }
}
