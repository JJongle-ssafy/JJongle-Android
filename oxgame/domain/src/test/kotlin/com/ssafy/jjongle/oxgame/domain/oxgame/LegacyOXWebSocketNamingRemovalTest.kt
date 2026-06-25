package com.ssafy.jjongle.oxgame.domain.oxgame

import com.ssafy.jjongle.oxgame.domain.repository.OXGameRepository
import com.ssafy.jjongle.oxgame.domain.usecase.GameActionUseCase
import com.ssafy.jjongle.oxgame.domain.usecase.StartOXGameUseCase
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * LegacyOXWebSocketNamingRemoval의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class LegacyOXWebSocketNamingRemovalTest {

    @Test
    fun ox_game_domain_contract_does_not_expose_websocket_lifecycle_names() {
        val legacyNames = setOf("connectWebSocket", "disconnectWebSocket")

        val publicMethodNames = listOf(
            OXGameRepository::class.java.methods.map { it.name },
            StartOXGameUseCase::class.java.methods.map { it.name },
            GameActionUseCase::class.java.methods.map { it.name }
        ).flatten().toSet()

        legacyNames.forEach { legacyName ->
            assertFalse("$legacyName should be removed from OX game domain API", legacyName in publicMethodNames)
        }
    }
}
