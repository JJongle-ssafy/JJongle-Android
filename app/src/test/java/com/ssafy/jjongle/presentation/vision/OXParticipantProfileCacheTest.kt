package com.ssafy.jjongle.oxgame.presentation.vision

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * OXParticipantProfileCache의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class OXParticipantProfileCacheTest {
    @Test
    fun updateFrom_keepsFirstProfileForParticipant() {
        val cache = OXParticipantProfileCache()

        cache.updateFrom(
            listOf(
                OXTrackedFace(
                    participantId = 1,
                    x = 0.2,
                    y = 0.4,
                    area = OXAnswerArea.O,
                    profileImageBase64 = "first"
                )
            )
        )
        val profiles = cache.updateFrom(
            listOf(
                OXTrackedFace(
                    participantId = 1,
                    x = 0.8,
                    y = 0.4,
                    area = OXAnswerArea.X,
                    profileImageBase64 = "second"
                )
            )
        )

        assertEquals("first", profiles[1])
    }

    @Test
    fun updateFrom_ignoresBlankProfiles() {
        val cache = OXParticipantProfileCache()

        val profiles = cache.updateFrom(
            listOf(
                OXTrackedFace(
                    participantId = 1,
                    x = 0.2,
                    y = 0.4,
                    area = OXAnswerArea.O,
                    profileImageBase64 = ""
                )
            )
        )

        assertEquals(emptyMap<Int, String>(), profiles)
    }
}
