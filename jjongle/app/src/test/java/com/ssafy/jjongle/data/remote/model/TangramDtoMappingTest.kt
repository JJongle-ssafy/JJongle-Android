package com.ssafy.jjongle.data.remote.model

import com.ssafy.jjongle.common.entity.AnimalType
import org.junit.Assert.assertEquals
import org.junit.Test

class TangramDtoMappingTest {
    @Test
    fun tangramHistoryItemDto_toDomain_mapsAllFields() {
        val domain = TangramHistoryItemDto(
            stage = 2,
            tangramId = 10L,
            animal = "RABBIT"
        ).toDomain()

        assertEquals(2, domain.stage)
        assertEquals(10L, domain.tangramId)
        assertEquals(AnimalType.RABBIT, domain.animal)
    }

    @Test
    fun tangramHistoryItemDto_toDomain_usesFallbacksForMissingFields() {
        val domain = TangramHistoryItemDto().toDomain()

        assertEquals(-1, domain.stage)
        assertEquals(-1L, domain.tangramId)
        assertEquals(AnimalType.TURTLE, domain.animal)
    }

    @Test
    fun tangramDetailResponse_toDomain_usesFallbackForMissingStory() {
        val domain = TangramDetailResponse().toDomain(id = 7L, type = AnimalType.DOG)

        assertEquals(7L, domain.tangramId)
        assertEquals(AnimalType.DOG, domain.animal)
        assertEquals("[MISSING_SERVER_FIELD:tangram.story]", domain.story)
    }
}
