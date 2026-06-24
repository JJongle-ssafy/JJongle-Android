package com.ssafy.jjongle.presentation.ui.screen

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class CameraScreenOrientationConfigTest {

    @Test
    fun `main activity keeps tablet landscape without runtime orientation override`() {
        val manifest = projectFile("src/main/AndroidManifest.xml").readText()
        val mainActivity = projectFile("src/main/java/com/ssafy/jjongle/MainActivity.kt").readText()

        assertTrue(manifest.contains("""android:screenOrientation="landscape""""))
        assertFalse(mainActivity.contains("requestedOrientation"))
        assertFalse(mainActivity.contains("SCREEN_ORIENTATION_LANDSCAPE"))
    }

    @Test
    fun `camera screen configures CameraX target rotation`() {
        val source = projectFile(
            "src/main/java/com/ssafy/jjongle/presentation/ui/screen/CameraScreen.kt"
        ).readText()

        assertTrue(source.contains("val targetRotation ="))
        assertTrue(
            Regex("""Preview\.Builder\(\)[\s\S]*?\.setTargetRotation\(targetRotation\)""")
                .containsMatchIn(source)
        )
        assertTrue(
            Regex("""ImageAnalysis\.Builder\(\)[\s\S]*?\.setTargetRotation\(targetRotation\)""")
                .containsMatchIn(source)
        )
    }

    @Test
    fun `manifest opts out of Android camera compatibility forced rotation`() {
        val manifest = projectFile("src/main/AndroidManifest.xml").readText()

        assertTrue(
            manifest.contains(
                "android:name=\"android.window.PROPERTY_CAMERA_COMPAT_ALLOW_FORCE_ROTATION\""
            )
        )
        assertTrue(
            manifest.contains(
                "android:name=\"android.window.PROPERTY_CAMERA_COMPAT_ALLOW_SIMULATE_REQUESTED_ORIENTATION\""
            )
        )
        assertTrue(
            Regex(
                "PROPERTY_CAMERA_COMPAT_ALLOW_FORCE_ROTATION\"[\\s\\S]*?android:value=\"false\""
            ).containsMatchIn(manifest)
        )
        assertTrue(
            Regex(
                "PROPERTY_CAMERA_COMPAT_ALLOW_SIMULATE_REQUESTED_ORIENTATION\"[\\s\\S]*?android:value=\"false\""
            ).containsMatchIn(manifest)
        )
    }

    private fun projectFile(pathFromModule: String): File {
        val userDir = File(requireNotNull(System.getProperty("user.dir")))
        val root = generateSequence(userDir) { it.parentFile }
            .first { it.resolve("settings.gradle.kts").exists() }
        return sequenceOf(
            userDir.resolve(pathFromModule),
            root.resolve("app/$pathFromModule"),
            root.resolve("main/presentation/$pathFromModule")
        ).firstOrNull { it.exists() }
            ?: error("Cannot find $pathFromModule from ${userDir.absolutePath}")
    }
}
