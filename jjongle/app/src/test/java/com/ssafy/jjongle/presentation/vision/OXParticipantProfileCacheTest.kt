package com.ssafy.jjongle.presentation.vision

import org.junit.Assert.assertEquals
import org.junit.Test

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
