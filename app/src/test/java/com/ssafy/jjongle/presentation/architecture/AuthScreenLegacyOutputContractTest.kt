package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Auth Screen Legacy Output Contract Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class AuthScreenLegacyOutputContractTest {

    @Test
    fun auth_screens_and_main_activity_do_not_use_console_or_android_log_output() {
        val files = listOf(
            sourcePath("main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/LoginScreen.kt"),
            sourcePath("main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/SignupScreen.kt"),
            sourcePath("main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/SettingScreen.kt"),
            sourcePath("app/src/main/java/com/ssafy/jjongle/MainActivity.kt"),
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

    @Test
    fun login_local_auth_failures_use_mvi_error_state_instead_of_toast() {
        val loginSource = sourcePath(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/LoginScreen.kt"
        ).readText()
        val intentSource = sourcePath(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/viewmodel/AuthIntent.kt"
        ).readText()
        val viewModelSource = sourcePath(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/viewmodel/AuthViewModel.kt"
        ).readText()

        assertFalse(
            "LoginScreen local auth failures must not bypass MVI with Android Toast",
            loginSource.contains("android.widget.Toast") || loginSource.contains("Toast.makeText"),
        )
        assertTrue(
            "LoginScreen must render AuthViewModel error state",
            loginSource.contains("errorMessage = authState.error"),
        )
        assertTrue(
            "LoginScreen local Google auth failures must enter AuthViewModel through AuthIntent.ShowError",
            loginSource.contains("onIntent(AuthIntent.ShowError("),
        )
        assertTrue(
            "AuthIntent must expose a local error intent for SDK-side failures",
            intentSource.contains("class ShowError(val message: String)"),
        )
        assertTrue(
            "AuthViewModel must reduce local auth errors into uiState",
            viewModelSource.contains("is AuthIntent.ShowError -> dispatch(AuthReducerEvent.Failed(intent.message))"),
        )
    }

    private fun sourcePath(relativePath: String): Path {
        val root = generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
        return root.resolve(relativePath)
    }
}
