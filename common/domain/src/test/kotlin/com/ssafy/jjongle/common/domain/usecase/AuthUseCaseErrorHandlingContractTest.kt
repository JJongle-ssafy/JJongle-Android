package com.ssafy.jjongle.common.domain.usecase

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * AuthUseCaseErrorHandling의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class AuthUseCaseErrorHandlingContractTest {

    @Test
    fun auth_use_case_routes_all_repository_calls_through_base_use_case_common_result_wrapper() {
        val source = sourcePath(
            "common/domain/src/main/kotlin/com/ssafy/jjongle/common/domain/usecase/AuthUseCase.kt"
        ).readText()

        assertEquals(
            "AuthUseCase should use BaseUseCase result wrapper for all auth repository calls",
            6,
            Regex("""executeWithCommonHttpHandling\s*\{""").findAll(source).count(),
        )
        assertTrue(source.contains("executeWithCommonHttpHandling { authRepository.login(idToken) }"))
        assertTrue(source.contains("executeWithCommonHttpHandling { authRepository.signup(idToken, nickname, profileImage) }"))
        assertTrue(source.contains("executeWithCommonHttpHandling { authRepository.updateProfile(nickname, profileImage) }"))
        assertTrue(source.contains("executeWithCommonHttpHandling { authRepository.withdraw() }"))
        assertTrue(source.contains("executeWithCommonHttpHandling { authRepository.logout() }"))
        assertTrue(source.contains("executeWithCommonHttpHandling { authRepository.checkAuthStatus() }"))
        assertFalse(
            "AuthUseCase should not keep a private common HTTP helper after BaseUseCase owns the wrapper",
            source.contains("handleCommonHttpError"),
        )
        assertFalse(
            "AuthUseCase should not duplicate try/catch Result wrapping for repository calls",
            Regex("""=\s*try\s*\{""").containsMatchIn(source),
        )
    }

    private fun sourcePath(relativePath: String): Path {
        val root = generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
        return root.resolve(relativePath)
    }
}
