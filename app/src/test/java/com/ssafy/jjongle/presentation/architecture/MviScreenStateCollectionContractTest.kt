package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * MviScreenStateCollection의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class MviScreenStateCollectionContractTest {

    @Test
    fun mvi_screens_collect_ui_state_with_lifecycle_aware_api() {
        val screens = listOf(
            sourcePath("tangram/presentation/src/main/java/com/ssafy/jjongle/tangram/presentation/ui/screen/TangramStageScreen.kt"),
            sourcePath("main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/QuizNoteScreen.kt"),
            sourcePath("main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/AnimalBookScreen.kt"),
            sourcePath("main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/MapScreen.kt"),
            sourcePath("main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/SignupScreen.kt"),
            sourcePath("main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/SplashScreen.kt"),
            sourcePath("main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/LoginScreen.kt"),
            sourcePath("main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/MypageScreen.kt"),
            sourcePath("main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/SettingScreen.kt"),
        )

        screens.forEach { screen ->
            val source = screen.readText()

            assertTrue(
                "${screen.name} must use collectAsStateWithLifecycle for MVI uiState",
                source.contains("collectAsStateWithLifecycle("),
            )
            assertFalse(
                "${screen.name} must not use plain collectAsState for MVI uiState",
                source.contains("collectAsState("),
            )
        }
    }

    @Test
    fun mvi_view_models_do_not_expose_feature_state_alias_flows() {
        val viewModels = listOf(
            sourcePath("main/presentation/src/main/java/com/ssafy/jjongle/presentation/viewmodel/AuthViewModel.kt"),
            sourcePath("main/presentation/src/main/java/com/ssafy/jjongle/presentation/viewmodel/MapViewModel.kt"),
        )

        viewModels.forEach { viewModel ->
            val source = viewModel.readText()

            assertFalse(
                "${viewModel.name} must expose state through MviViewModel.uiState only",
                Regex("""val\s+(authState|mapState)\s*:""").containsMatchIn(source),
            )
        }
    }

    @Test
    fun ox_game_screen_collects_only_mvi_ui_state_from_view_model() {
        val screen = sourcePath(
            "oxgame/presentation/src/main/java/com/ssafy/jjongle/oxgame/presentation/ui/screen/OXGameScreen.kt",
        )
        val source = screen.readText()

        assertTrue(
            "OXGameScreen must collect the MVI uiState",
            source.contains("viewModel.uiState.collectAsStateWithLifecycle()"),
        )
        assertFalse(
            "OXGameScreen must not collect connectionState outside uiState",
            source.contains("viewModel.connectionState.collectAsStateWithLifecycle()"),
        )
    }

    private fun sourcePath(relativePath: String): Path {
        val root = generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
        return root.resolve(relativePath)
    }
}
