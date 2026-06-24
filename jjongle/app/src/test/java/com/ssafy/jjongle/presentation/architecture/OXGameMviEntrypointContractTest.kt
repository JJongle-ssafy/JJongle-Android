package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OXGameMviEntrypointContractTest {

    @Test
    fun ox_game_presentation_code_lives_in_feature_presentation_module_not_app() {
        val settings = sourcePath("settings.gradle.kts").readText()
        assertTrue(
            "settings.gradle.kts must include :oxgame:presentation",
            settings.contains("include(\":oxgame:presentation\")"),
        )

        val appPresentationRoot = sourcePath("app/src/main/java/com/ssafy/jjongle/presentation")
        val forbiddenAppFiles = listOf(
            "state/GameState.kt",
            "state/OXGameUiState.kt",
            "ui/screen/OXGameScreen.kt",
            "ui/component/CameraComponent.kt",
            "viewmodel/OXGameIntent.kt",
            "viewmodel/OXGameReducerEvent.kt",
            "viewmodel/OXGameViewModel.kt",
            "vision/FaceReidentifier.kt",
            "vision/HungarianAlgorithm.kt",
            "vision/KalmanBoxTracker.kt",
            "vision/OXFacePositionClassifier.kt",
            "vision/OXParticipantProfileCache.kt",
        )

        forbiddenAppFiles.forEach { relativePath ->
            assertFalse(
                "OXGame presentation file must not remain in :app: $relativePath",
                Files.exists(appPresentationRoot.resolve(relativePath)),
            )
        }

        assertTrue(
            "OXGameScreen must live in :oxgame:presentation",
            Files.exists(
                sourcePath(
                    "oxgame/presentation/src/main/java/com/ssafy/jjongle/oxgame/presentation/ui/screen/OXGameScreen.kt"
                )
            ),
        )
    }

    @Test
    fun ox_game_screen_enters_game_through_single_mvi_intent() {
        val source = oxGameScreenSource()

        assertTrue(
            "OXGameScreen must enter through OXGameIntent.EnterGame",
            source.contains("onIntent(OXGameIntent.EnterGame)"),
        )
        assertFalse(
            "OXGameScreen must not reset connection state directly",
            source.contains("viewModel.resetConnectionState()"),
        )
        assertFalse(
            "OXGameScreen must not call connectToGame() directly",
            source.contains("viewModel.connectToGame()"),
        )
    }

    @Test
    fun ox_game_screen_starts_quiz_through_mvi_intent() {
        val source = oxGameScreenSource()

        assertTrue(
            "OXGameScreen must start quiz through OXGameIntent.StartCurrentQuiz",
            source.contains("onIntent(OXGameIntent.StartCurrentQuiz)"),
        )
        assertFalse(
            "OXGameScreen must not call startCurrentQuiz() directly",
            source.contains("viewModel.startCurrentQuiz()"),
        )
    }

    @Test
    fun ox_game_screen_clears_error_through_mvi_intent() {
        val source = oxGameScreenSource()

        assertTrue(
            "OXGameScreen must clear errors through OXGameIntent.ClearError",
            source.contains("onIntent(OXGameIntent.ClearError)"),
        )
        assertFalse(
            "OXGameScreen must not call clearError() directly",
            source.contains("viewModel.clearError()"),
        )
    }

    @Test
    fun ox_game_screen_restarts_game_through_mvi_intent() {
        val source = oxGameScreenSource()

        assertTrue(
            "OXGameScreen must restart through OXGameIntent.RestartGame",
            source.contains("onIntent(OXGameIntent.RestartGame)"),
        )
        assertFalse(
            "OXGameScreen must not call restartGame() directly",
            source.contains("viewModel.restartGame()"),
        )
    }

    @Test
    fun ox_game_screen_moves_to_next_quiz_through_mvi_intent() {
        val source = oxGameScreenSource()

        assertTrue(
            "OXGameScreen must move to next quiz through OXGameIntent.NextQuiz",
            source.contains("onIntent(OXGameIntent.NextQuiz)"),
        )
        assertFalse(
            "OXGameScreen must not call nextQuiz() directly",
            source.contains("viewModel.nextQuiz()"),
        )
    }

    @Test
    fun ox_game_screen_shows_explanation_through_mvi_intent() {
        val source = oxGameScreenSource()

        assertTrue(
            "OXGameScreen must show explanation through OXGameIntent.ShowExplanation",
            source.contains("onIntent(OXGameIntent.ShowExplanation)"),
        )
        assertFalse(
            "OXGameScreen must not call showExplanation() directly",
            source.contains("viewModel.showExplanation()"),
        )
    }

    @Test
    fun ox_game_screen_updates_tracked_faces_through_mvi_intent() {
        val source = oxGameScreenSource()

        assertTrue(
            "OXGameScreen must update tracked faces through OXGameIntent.UpdateTrackedFaces",
            source.contains("onIntent(OXGameIntent.UpdateTrackedFaces("),
        )
        assertFalse(
            "OXGameScreen must not pass updateTrackedFaces directly",
            source.contains("viewModel::updateTrackedFaces"),
        )
    }

    @Test
    fun ox_game_screen_collects_state_with_lifecycle_aware_api() {
        val source = oxGameScreenSource()

        assertTrue(
            "OXGameScreen must collect ViewModel state with lifecycle-aware Compose API",
            source.contains("collectAsStateWithLifecycle("),
        )
        assertFalse(
            "OXGameScreen must not use plain collectAsState() for ViewModel StateFlow",
            source.contains("collectAsState()"),
        )
    }

    @Test
    fun ox_game_screen_reads_game_state_from_single_mvi_ui_state() {
        val source = oxGameScreenSource()
        val forbiddenDirectStateCollections = listOf(
            "viewModel.gameState.collectAsStateWithLifecycle()",
            "viewModel.quizSession.collectAsStateWithLifecycle()",
            "viewModel.currentQuiz.collectAsStateWithLifecycle()",
            "viewModel.currentQuizIndex.collectAsStateWithLifecycle()",
            "viewModel.isLoading.collectAsStateWithLifecycle()",
            "viewModel.errorMessage.collectAsStateWithLifecycle()",
            "viewModel.timeLeft.collectAsStateWithLifecycle()",
            "viewModel.isQuizActive.collectAsStateWithLifecycle()",
            "viewModel.finishProfiles.collectAsStateWithLifecycle()",
            "viewModel.finalTop3.collectAsStateWithLifecycle()",
            "viewModel.userPosition.collectAsStateWithLifecycle()",
            "viewModel.isAnswerSubmitted.collectAsStateWithLifecycle()",
            "viewModel.showRewardAnimation.collectAsStateWithLifecycle()",
            "viewModel.animationType.collectAsStateWithLifecycle()",
        )

        assertTrue(
            "OXGameScreen must collect MviViewModel.uiState",
            source.contains("viewModel.uiState.collectAsStateWithLifecycle()"),
        )
        forbiddenDirectStateCollections.forEach { directCollection ->
            assertFalse(
                "OXGameScreen must read game state from uiState, not $directCollection",
                source.contains(directCollection),
            )
        }
    }

    @Test
    fun ox_game_view_model_keeps_game_actions_private_behind_on_intent() {
        val source = sourcePath(
            "oxgame/presentation/src/main/java/com/ssafy/jjongle/oxgame/presentation/viewmodel/OXGameViewModel.kt"
        ).readText()
        val actionNames = listOf(
            "connectToGame",
            "clearError",
            "startCurrentQuiz",
            "updateTrackedFaces",
            "showExplanation",
            "nextQuiz",
            "resetConnectionState",
            "restartGame",
        )

        actionNames.forEach { actionName ->
            assertTrue(
                "$actionName must be private and reachable through OXGameIntent",
                source.contains(Regex("\\bprivate fun $actionName\\s*\\(")),
            )
            assertFalse(
                "$actionName must not remain as a public ViewModel action",
                source.contains(Regex("(?m)^\\s+fun $actionName\\s*\\(")),
            )
        }
    }

    @Test
    fun ox_game_view_model_exposes_game_state_only_through_mvi_ui_state() {
        val source = sourcePath(
            "oxgame/presentation/src/main/java/com/ssafy/jjongle/oxgame/presentation/viewmodel/OXGameViewModel.kt"
        ).readText()
        val legacyStateNames = listOf(
            "gameState",
            "quizSession",
            "currentQuizIndex",
            "currentQuiz",
            "isLoading",
            "errorMessage",
            "timeLeft",
            "isQuizActive",
            "gameScore",
            "quizResults",
            "finalTop3",
            "latestTrackedFaces",
            "showRewardAnimation",
            "animationType",
            "userPosition",
            "isAnswerSubmitted",
            "finishProfiles",
            "gameEvents",
            "connectionState",
        )

        legacyStateNames.forEach { stateName ->
            assertFalse(
                "OXGameViewModel must not expose legacy public state '$stateName'; route it through uiState/onIntent",
                source.contains(Regex("(?m)^ {4}val $stateName\\s*[:=]")),
            )
        }
    }

    @Test
    fun ox_game_view_model_does_not_keep_private_state_flow_mirrors_of_ui_state() {
        val source = sourcePath(
            "oxgame/presentation/src/main/java/com/ssafy/jjongle/oxgame/presentation/viewmodel/OXGameViewModel.kt"
        ).readText()
        val mirroredStateNames = listOf(
            "_gameState",
            "_quizSession",
            "_currentQuizIndex",
            "_isLoading",
            "_errorMessage",
            "_timeLeft",
            "_isQuizActive",
            "_gameScore",
            "_quizResults",
            "_finalTop3",
            "_showRewardAnimation",
            "_animationType",
            "_userPosition",
            "_isAnswerSubmitted",
            "_finishProfiles",
        )

        assertFalse(
            "OXGameViewModel must not import MutableStateFlow for private mirrors of MVI uiState",
            source.contains("import kotlinx.coroutines.flow.MutableStateFlow"),
        )
        mirroredStateNames.forEach { stateName ->
            assertFalse(
                "OXGameViewModel must not keep private StateFlow mirror '$stateName'; read currentState instead",
                source.contains(stateName),
            )
        }
    }

    @Test
    fun ox_game_uses_session_quiz_count_without_temporary_three_question_residue() {
        val viewModel = sourcePath(
            "oxgame/presentation/src/main/java/com/ssafy/jjongle/oxgame/presentation/viewmodel/OXGameViewModel.kt"
        ).readText()
        val screen = oxGameScreenSource()
        val combined = "$viewModel\n$screen"

        assertTrue(
            "OXGameViewModel must advance using the quiz session size from state",
            viewModel.contains("currentState.currentQuizIndex < session.quizzes.size - 1"),
        )
        assertTrue(
            "OXGameScreen must render progress and result transitions from totalQuizzes",
            screen.contains("text = \"문제 ${'$'}{quizIndex + 1}/${'$'}totalQuizzes\"") &&
                screen.contains("quizIndex + 1 >= totalQuizzes"),
        )
        assertFalse(
            "OXGame production code must not keep temporary three-question TODOs",
            combined.contains("TODO: 문제 수 3") ||
                combined.contains("문제 수 3 문제로 조정") ||
                combined.contains("currentQuizIndex < 2") ||
                combined.contains("quizIndex + 1 >= 3"),
        )
        assertFalse(
            "OXGame production comments must not describe current GameFinish handling as legacy",
            combined.contains("Legacy GameFinish") || combined.contains("legacy GameFinish"),
        )
    }

    private fun sourcePath(relativePath: String): Path {
        val root = generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
        return root.resolve(relativePath)
    }

    private fun oxGameScreenSource(): String {
        return sourcePath(
            "oxgame/presentation/src/main/java/com/ssafy/jjongle/oxgame/presentation/ui/screen/OXGameScreen.kt"
        ).readText()
    }
}
