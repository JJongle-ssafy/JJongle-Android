package com.ssafy.jjongle.oxgame.domain.oxgame

import com.ssafy.jjongle.oxgame.domain.repository.OXGameRepository
import com.ssafy.jjongle.oxgame.domain.usecase.GameActionUseCase
import com.ssafy.jjongle.oxgame.domain.usecase.StartOXGameUseCase
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * Legacy OXWeb Socket Naming Removal Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
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
