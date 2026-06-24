package com.ssafy.jjongle.data.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Test

class AuthDataLegacyOutputContractTest {

    @Test
    fun auth_data_boundary_does_not_use_direct_console_or_android_log_output() {
        val files = listOf(
            sourcePath("common/data/src/main/java/com/ssafy/jjongle/common/data/repository/AuthRepositoryImpl.kt"),
            sourcePath("common/data/src/main/java/com/ssafy/jjongle/common/data/service/GoogleAuthServiceImpl.kt"),
        )

        files.forEach { file ->
            val source = file.readText()
            assertFalse(
                "${file.name} must not use println for data-boundary diagnostics",
                source.contains("println("),
            )
            assertFalse(
                "${file.name} must not use Android Log directly for data-boundary diagnostics",
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
