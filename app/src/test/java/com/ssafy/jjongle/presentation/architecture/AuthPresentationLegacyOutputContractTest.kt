package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * AuthPresentationLegacyOutput의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class AuthPresentationLegacyOutputContractTest {

    @Test
    fun auth_view_model_does_not_use_console_or_android_log_output() {
        val source = sourcePath(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/viewmodel/AuthViewModel.kt",
        ).readText()

        assertFalse(
            "AuthViewModel must not use println for presentation-side diagnostics",
            source.contains("println("),
        )
        assertFalse(
            "AuthViewModel must not use Android Log directly for presentation-side diagnostics",
            Regex("""\bLog\.[devwi]\(""").containsMatchIn(source),
        )
    }

    private fun sourcePath(relativePath: String): Path {
        val root = generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
        return root.resolve(relativePath)
    }
}
