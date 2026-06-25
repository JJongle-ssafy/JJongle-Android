package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * AuthScreenStateCollection의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
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
