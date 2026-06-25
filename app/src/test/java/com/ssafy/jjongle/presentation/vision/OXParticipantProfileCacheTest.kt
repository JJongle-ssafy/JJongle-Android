package com.ssafy.jjongle.oxgame.presentation.vision

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * OXParticipant Profile Cache Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
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
