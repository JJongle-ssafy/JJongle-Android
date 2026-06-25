package com.ssafy.jjongle.data.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * GoogleAuthServiceErrorPropagation의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class GoogleAuthServiceErrorPropagationContractTest {

    @Test
    fun google_auth_service_throws_instead_of_returning_result_from_data_layer() {
        val root = repositoryRoot()
        val contract = root.resolve(
            "common/domain/src/main/kotlin/com/ssafy/jjongle/common/domain/repository/GoogleAuthService.kt"
        ).readText()
        val implementation = root.resolve(
            "common/data/src/main/java/com/ssafy/jjongle/common/data/service/GoogleAuthServiceImpl.kt"
        ).readText()
        val authViewModel = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/viewmodel/AuthViewModel.kt"
        ).readText()

        assertTrue(
            "GoogleAuthService.signIn should return GoogleUser and propagate failures by throwing",
            contract.contains("suspend fun signIn(idToken: String): GoogleUser"),
        )
        assertFalse(
            "GoogleAuthService must not expose Result from the domain contract",
            contract.contains("Result<GoogleUser>"),
        )
        assertFalse(
            "Data implementation must not catch and wrap Google sign-in failures as Result.failure",
            implementation.contains("Result.failure") || implementation.contains("Result.success"),
        )
        assertFalse(
            "Data implementation must not own Google sign-in try/catch error policy",
            Regex("""override\s+suspend\s+fun\s+signIn[\s\S]*?catch\s*\(""")
                .containsMatchIn(implementation),
        )
        assertTrue(
            "Presentation must own UI recovery for Google sign-in failures",
            authViewModel.contains("runCatching { googleAuthService.signIn(googleIdToken) }"),
        )
    }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
}
