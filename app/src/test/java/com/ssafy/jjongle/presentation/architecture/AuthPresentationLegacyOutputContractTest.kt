package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * Auth Presentation Legacy Output Contract Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
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
