package com.ssafy.jjongle.data.architecture

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Auth Error Domain Boundary Contract Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class AuthErrorDomainBoundaryContractTest {

    @Test
    fun auth_error_hierarchy_lives_in_domain_error_package_not_entity() {
        val entityDir = projectRoot()
            .resolve("common/entity/src/main/kotlin/com/ssafy/jjongle/common/entity")
        val domainAuthErrorDir = projectRoot()
            .resolve("common/domain/src/main/kotlin/com/ssafy/jjongle/common/domain/error/auth")
        val authRepository = source(
            "common/data/src/main/java/com/ssafy/jjongle/common/data/repository/AuthRepositoryImpl.kt"
        )
        val authViewModel = source(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/viewmodel/AuthViewModel.kt"
        )

        assertFalse(entityDir.resolve("AuthError.kt").exists())
        assertFalse(entityDir.resolve("AuthException.kt").exists())
        assertFalse(entityDir.resolve("MissingTokenAuthError.kt").exists())
        assertTrue(domainAuthErrorDir.resolve("AuthError.kt").exists())
        assertTrue(domainAuthErrorDir.resolve("AuthException.kt").exists())
        assertTrue(domainAuthErrorDir.resolve("MissingTokenAuthError.kt").exists())
        assertTrue(authRepository.contains("com.ssafy.jjongle.common.domain.error.auth.AuthException"))
        assertFalse(authRepository.contains("com.ssafy.jjongle.common.entity.AuthException"))
        assertTrue(authViewModel.contains("com.ssafy.jjongle.common.domain.error.auth.AuthException"))
        assertFalse(authViewModel.contains("com.ssafy.jjongle.common.entity.AuthException"))
    }

    private fun source(path: String): String = projectRoot().resolve(path).readText()

    private fun projectRoot(): File {
        var current = File(".").absoluteFile
        while (current.parentFile != null) {
            if (current.resolve("settings.gradle.kts").exists()) return current
            current = current.parentFile!!
        }
        error("Project root not found")
    }
}
