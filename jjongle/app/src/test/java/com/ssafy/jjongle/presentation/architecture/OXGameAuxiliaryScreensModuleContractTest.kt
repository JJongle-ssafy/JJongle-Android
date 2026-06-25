package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * OXGameAuxiliaryScreensModule의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class OXGameAuxiliaryScreensModuleContractTest {

    @Test
    fun ox_game_title_and_tutorial_screens_live_in_feature_presentation_module() {
        val root = repositoryRoot()
        val appScreenRoot = root.resolve("app/src/main/java/com/ssafy/jjongle/presentation/ui/screen")

        listOf(
            "OXGameTitleScreen.kt",
            "OXTutorialScreen.kt",
        ).forEach { fileName ->
            assertFalse(
                "OXGame auxiliary screen must not remain in :app: $fileName",
                Files.exists(appScreenRoot.resolve(fileName)),
            )
            assertTrue(
                "OXGame auxiliary screen must live in :oxgame:presentation: $fileName",
                Files.exists(
                    root.resolve(
                        "oxgame/presentation/src/main/java/com/ssafy/jjongle/oxgame/presentation/ui/screen/$fileName"
                    )
                ),
            )
        }
    }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
}
