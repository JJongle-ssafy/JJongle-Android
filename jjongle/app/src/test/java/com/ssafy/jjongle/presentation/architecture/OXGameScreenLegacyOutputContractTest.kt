package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * OXGameScreenLegacyOutput의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class OXGameScreenLegacyOutputContractTest {

    @Test
    fun ox_game_screen_and_camera_component_do_not_use_console_or_android_log_output() {
        val files = listOf(
            sourcePath("oxgame/presentation/src/main/java/com/ssafy/jjongle/oxgame/presentation/ui/screen/OXGameScreen.kt"),
            sourcePath("oxgame/presentation/src/main/java/com/ssafy/jjongle/oxgame/presentation/ui/component/CameraComponent.kt"),
        )

        files.forEach { file ->
            val source = file.readText()
            assertFalse(
                "${file.name} must not use println for presentation-side diagnostics",
                source.contains("println("),
            )
            assertFalse(
                "${file.name} must not use Android Log directly for presentation-side diagnostics",
                Regex("""\b(?:android\.util\.)?Log\.[devwi]\(""").containsMatchIn(source),
            )
        }
    }

    private fun sourcePath(relativePath: String): Path {
        val root = generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
        return root.resolve(relativePath)
    }
}
