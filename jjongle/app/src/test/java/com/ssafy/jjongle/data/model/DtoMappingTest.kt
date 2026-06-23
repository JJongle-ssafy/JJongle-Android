package com.ssafy.jjongle.data.model

import com.ssafy.jjongle.common.entity.UserPosition
import org.junit.Assert.assertEquals
import org.junit.Test

class DtoMappingTest {
    @Test
    fun userPositionDto_toDomain_mapsAllFields() {
        val dto = UserPositionDto(userId = 7, x = 0.25, y = 0.75)

        val domain = dto.toDomain()

        assertEquals(UserPosition(userId = 7, x = 0.25, y = 0.75), domain)
    }

    @Test
    fun userPositionDto_toDomain_usesFallbacksForMissingFields() {
        val domain = UserPositionDto().toDomain()

        assertEquals(UserPosition(userId = -1, x = 0.0, y = 0.0), domain)
    }

    @Test
    fun userPosition_toDto_mapsAllFields() {
        val domain = UserPosition(userId = 9, x = 0.1, y = 0.9)

        val dto = domain.toDto()

        assertEquals(UserPositionDto(userId = 9, x = 0.1, y = 0.9), dto)
    }

    @Test
    fun gameFinishProfile_toDomain_mapsAllFields() {
        val dto = GameFinishProfile(userId = 3, base64 = "profile-image")

        val domain = dto.toDomain()

        assertEquals(3, domain.userId)
        assertEquals("profile-image", domain.base64)
    }

    @Test
    fun gameFinishProfile_toDomain_usesFallbacksForMissingFields() {
        val domain = GameFinishProfile().toDomain()

        assertEquals(-1, domain.userId)
        assertEquals("[MISSING_SERVER_FIELD:gameFinish.base64]", domain.base64)
    }

    @Test
    fun gameStartResponse_toDomain_usesFallbacksForMissingData() {
        val domain = GameStartResponse().toDomain()

        assertEquals("[MISSING_SERVER_FIELD:gameStart.sessionKey]", domain.sessionKey)
        assertEquals(emptyList<Any>(), domain.quizzes)
    }

    @Test
    fun quizResponse_toDomain_usesFallbacksForMissingFields() {
        val domain = QuizResponse().toDomain()

        assertEquals(-1, domain.id)
        assertEquals("[MISSING_SERVER_FIELD:quiz.question]", domain.question)
        assertEquals("[MISSING_SERVER_FIELD:quiz.answer]", domain.answer)
        assertEquals("[MISSING_SERVER_FIELD:quiz.description]", domain.description)
    }
}
