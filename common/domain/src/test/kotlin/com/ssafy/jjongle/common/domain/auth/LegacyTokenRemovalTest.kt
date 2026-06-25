package com.ssafy.jjongle.common.domain.auth

import com.ssafy.jjongle.common.domain.repository.AuthRepository
import com.ssafy.jjongle.common.entity.AuthState
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * Legacy Token Removal Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class LegacyTokenRemovalTest {

    @Test
    fun auth_state_does_not_expose_server_issued_tokens() {
        val propertyNames = AuthState::class.java.declaredFields.map { it.name }.toSet()

        assertFalse("AuthState.accessToken should be removed", "accessToken" in propertyNames)
        assertFalse("AuthState.refreshToken should be removed", "refreshToken" in propertyNames)
    }

    @Test
    fun auth_repository_does_not_expose_server_issued_token_api() {
        val methodNames = AuthRepository::class.java.methods.map { it.name }.toSet()

        assertFalse("AuthRepository.reissue should be removed", "reissue" in methodNames)
        assertFalse("AuthRepository.getStoredTokens should be removed", "getStoredTokens" in methodNames)
        assertFalse("AuthRepository.saveTokens should be removed", "saveTokens" in methodNames)
    }

    @Test
    fun legacy_token_usecases_are_not_on_classpath() {
        listOf(
            "com.ssafy.jjongle.common.domain.usecase.GetAuthTokensUseCase",
            "com.ssafy.jjongle.common.domain.usecase.SaveAuthTokensUseCase",
            "com.ssafy.jjongle.common.entity.AuthTokens",
        ).forEach { className ->
            assertClassIsAbsent(className)
        }
    }

    private fun assertClassIsAbsent(className: String) {
        try {
            Class.forName(className)
            throw AssertionError("$className should have been removed")
        } catch (_: ClassNotFoundException) {
        }
    }
}
