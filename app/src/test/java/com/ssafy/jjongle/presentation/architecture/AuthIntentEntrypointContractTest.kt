package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Auth Intent Entrypoint Contract Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class AuthIntentEntrypointContractTest {

    @Test
    fun mypage_logout_uses_auth_intent_entrypoint() {
        val source = sourcePath(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/MypageScreen.kt"
        ).readText()

        assertTrue(
            "MypageScreen logout must enter AuthViewModel through AuthIntent",
            source.contains("onIntent(AuthIntent.Logout)"),
        )
        assertFalse(
            "MypageScreen must not call AuthViewModel.logout() directly",
            source.contains("authViewModel.logout()"),
        )
    }

    @Test
    fun setting_profile_update_and_withdraw_use_auth_intent_entrypoint() {
        val source = sourcePath(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/SettingScreen.kt"
        ).readText()

        assertTrue(
            "SettingScreen profile update must enter AuthViewModel through AuthIntent",
            source.contains("onIntent(AuthIntent.UpdateProfile("),
        )
        assertTrue(
            "SettingScreen withdraw must enter AuthViewModel through AuthIntent",
            source.contains("onIntent(AuthIntent.Withdraw("),
        )
        assertFalse(
            "SettingScreen must not call AuthViewModel.updateProfile() directly",
            source.contains("authViewModel.updateProfile("),
        )
        assertFalse(
            "SettingScreen must not call AuthViewModel.withdraw() directly",
            source.contains("authViewModel.withdraw("),
        )
    }

    @Test
    fun login_google_auth_uses_auth_intent_entrypoint() {
        val source = sourcePath(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/LoginScreen.kt"
        ).readText()

        assertTrue(
            "LoginScreen Google auth must enter AuthViewModel through AuthIntent",
            source.contains("onIntent(AuthIntent.LoginWithGoogleIdToken("),
        )
        assertFalse(
            "LoginScreen must not call AuthViewModel.loginWithGoogleIdToken() directly",
            source.contains("viewModel.loginWithGoogleIdToken("),
        )
    }

    @Test
    fun signup_uses_auth_intent_entrypoint() {
        val source = sourcePath(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/SignupScreen.kt"
        ).readText()

        assertTrue(
            "SignupScreen sign-up must enter AuthViewModel through AuthIntent",
            source.contains("onIntent(AuthIntent.SignUp("),
        )
        assertFalse(
            "SignupScreen must not call AuthViewModel.signUp() directly",
            source.contains("authViewModel.signUp("),
        )
    }

    @Test
    fun auth_view_model_keeps_auth_actions_private_behind_on_intent() {
        val source = sourcePath(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/viewmodel/AuthViewModel.kt"
        ).readText()
        val privateActions = listOf(
            "checkAuthStatus",
            "login",
            "loginWithGoogleIdToken",
            "signUp",
            "logout",
            "updateProfile",
            "withdraw",
        )

        privateActions.forEach { action ->
            assertTrue(
                "AuthViewModel.${action} must be private and reachable through onIntent",
                source.contains("private fun $action("),
            )
            assertFalse(
                "AuthViewModel.${action} must not remain a public ViewModel action",
                source.contains("\n    fun $action("),
            )
        }
    }

    @Test
    fun auth_view_model_uses_auth_use_case_instead_of_repository_directly() {
        val source = sourcePath(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/viewmodel/AuthViewModel.kt"
        ).readText()

        assertTrue(
            "AuthViewModel must depend on AuthUseCase so domain handles auth errors",
            source.contains("AuthUseCase"),
        )
        assertFalse(
            "AuthViewModel must not import AuthRepository directly",
            source.contains("import com.ssafy.jjongle.common.domain.repository.AuthRepository"),
        )
        assertFalse(
            "AuthViewModel must not keep an AuthRepository constructor dependency",
            source.contains("private val authRepository: AuthRepository"),
        )
    }

    private fun sourcePath(relativePath: String): Path {
        val root = generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
        return root.resolve(relativePath)
    }
}
