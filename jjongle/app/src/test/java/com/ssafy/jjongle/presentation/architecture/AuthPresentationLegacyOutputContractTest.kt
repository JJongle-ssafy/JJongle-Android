package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Test

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
