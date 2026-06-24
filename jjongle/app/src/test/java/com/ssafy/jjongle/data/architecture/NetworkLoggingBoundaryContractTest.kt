package com.ssafy.jjongle.data.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkLoggingBoundaryContractTest {

    @Test
    fun pretty_http_logging_interceptor_uses_injected_logger_not_android_log_directly() {
        val source = sourcePath(
            "common/data/src/main/java/com/ssafy/jjongle/common/data/remote/PrettyHttpLoggingInterceptor.kt"
        ).readText()

        assertFalse(
            "common:data network logging must not import Android Log directly",
            source.contains("import android.util.Log"),
        )
        assertFalse(
            "PrettyHttpLoggingInterceptor must not call Android Log directly",
            Regex("""\bLog\.(?:v|i|d|w|e|println)\(""").containsMatchIn(source),
        )
        assertTrue(
            "PrettyHttpLoggingInterceptor must accept an injected logger boundary",
            source.contains("fun interface HttpLogger") &&
                source.contains("private val logger: HttpLogger = HttpLogger.NoOp"),
        )
        assertTrue(
            "Default network logger must be no-op unless an app/debug layer opts in",
            source.contains("val NoOp = HttpLogger { _, _, _ -> }"),
        )
    }

    private fun sourcePath(relativePath: String): Path {
        val root = generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
        return root.resolve(relativePath)
    }
}
