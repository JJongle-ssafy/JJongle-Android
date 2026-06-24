package com.ssafy.jjongle.common.domain.auth

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryErrorHandlingContractTest {

    @Test
    fun auth_repository_login_and_signup_do_not_wrap_results_in_data_layer() {
        val source = sourcePath(
            "common/domain/src/main/kotlin/com/ssafy/jjongle/common/domain/repository/AuthRepository.kt"
        ).readText()

        assertTrue(
            "AuthRepository.login should return AuthState and let AuthUseCase wrap Result",
            source.contains("suspend fun login(idToken: String): AuthState"),
        )
        assertTrue(
            "AuthRepository.signup should return AuthState and let AuthUseCase wrap Result",
            source.contains(
                "suspend fun signup(idToken: String, nickname: String, profileImage: String): AuthState"
            ),
        )
        assertFalse(
            "AuthRepository.login/signup must not return Result<AuthState> from data layer",
            source.contains("Result<AuthState>"),
        )
    }

    private fun sourcePath(relativePath: String): Path {
        val root = generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
        return root.resolve(relativePath)
    }
}
