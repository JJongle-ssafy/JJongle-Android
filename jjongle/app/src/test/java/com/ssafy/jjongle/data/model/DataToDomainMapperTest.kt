package com.ssafy.jjongle.data.model

import com.ssafy.jjongle.domain.entity.UserPosition
import org.junit.Assert.assertEquals
import org.junit.Test

class DataToDomainMapperTest {
    @Test
    fun userPositionDto_toDomain_mapsAllFields() {
        val dto = UserPositionDto(userId = 7, x = 0.25, y = 0.75)

        val domain = dto.toDomain()

        assertEquals(UserPosition(userId = 7, x = 0.25, y = 0.75), domain)
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
}
