package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CameraScreenLegacyOutputContractTest {

    @Test
    fun camera_capture_messages_use_compose_state_instead_of_android_toast() {
        val source = sourcePath(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/CameraScreen.kt"
        ).readText()

        assertFalse(
            "CameraScreen must not bypass Compose UI state with Android Toast",
            source.contains("android.widget.Toast") || source.contains("Toast.makeText"),
        )
        assertTrue(
            "CameraScreen must keep capture output in Compose state",
            source.contains("var captureMessage by remember { mutableStateOf<String?>(null) }"),
        )
        assertTrue(
            "CameraScreen chrome must render capture messages with the design-system text component",
            source.contains("captureMessage?.let") && source.contains("ArchiText("),
        )
    }

    private fun sourcePath(relativePath: String): Path {
        val root = generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
        return root.resolve(relativePath)
    }
}
