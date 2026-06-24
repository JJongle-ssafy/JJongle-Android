package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthScreenStateCollectionContractTest {

    @Test
    fun auth_screens_collect_auth_state_with_lifecycle_aware_api() {
        val screens = listOf(
            sourcePath("main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/LoginScreen.kt"),
            sourcePath("main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/SplashScreen.kt"),
            sourcePath("main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/MypageScreen.kt"),
            sourcePath("main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/SettingScreen.kt"),
        )

        screens.forEach { screen ->
            val source = screen.readText()
            assertTrue(
                "${screen.name} must use collectAsStateWithLifecycle for AuthViewModel state",
                source.contains("collectAsStateWithLifecycle("),
            )
            assertFalse(
                "${screen.name} must not use plain collectAsState for AuthViewModel state",
                source.contains("collectAsState("),
            )
        }
    }

    private fun sourcePath(relativePath: String): Path {
        val root = generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
        return root.resolve(relativePath)
    }
}
