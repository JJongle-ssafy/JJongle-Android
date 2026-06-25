package com.ssafy.jjongle.tangram.data.remote.model

import com.ssafy.jjongle.common.entity.AnimalType
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tangram Dto Mapping Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class TangramDtoMappingTest {
    @Test
    fun singleGameDto_toVO_usesFallbackForMissingStage() {
        assertEquals(1, SingleGameDTO().toVO())
        assertEquals(5, SingleGameDTO(stage = 5).toVO())
    }

    @Test
    fun tangramHistoryItemDto_toVO_mapsAllFields() {
        val domain = TangramHistoryItemDTO(
            stage = 2,
            tangramId = 10L,
            animal = "RABBIT"
        ).toVO()

        assertEquals(2, domain.stage)
        assertEquals(10L, domain.tangramId)
        assertEquals(AnimalType.RABBIT, domain.animal)
    }

    @Test
    fun tangramHistoryItemDto_toVO_usesFallbacksForMissingFields() {
        val domain = TangramHistoryItemDTO().toVO()

        assertEquals(-1, domain.stage)
        assertEquals(-1L, domain.tangramId)
        assertEquals(AnimalType.TURTLE, domain.animal)
    }

    @Test
    fun tangramDetailDto_toVO_usesFallbackForMissingStory() {
        val domain = TangramDetailDTO().toVO(id = 7L, type = AnimalType.DOG)

        assertEquals(7L, domain.tangramId)
        assertEquals(AnimalType.DOG, domain.animal)
        assertEquals("[MISSING_SERVER_FIELD:tangram.story]", domain.story)
    }

    @Test
    fun tangramHistoriesPageDto_toVO_exposesPaginationEndSignal() {
        val page = TangramHistoriesPageDTO(
            content = listOf(TangramHistoryItemDTO(stage = 1, tangramId = 2L, animal = "DOG")),
            isLast = true,
        ).toVO()

        assertEquals(1, page.content.size)
        assertEquals(AnimalType.DOG, page.content.first().animal)
        assertEquals(true, page.isEnd)
    }

    @Test
    fun tangramHistoriesPageDto_toVO_treatsEmptyContentAsEnd() {
        val page = TangramHistoriesPageDTO(content = emptyList(), isLast = false).toVO()

        assertEquals(emptyList<Any>(), page.content)
        assertEquals(true, page.isEnd)
    }
}
