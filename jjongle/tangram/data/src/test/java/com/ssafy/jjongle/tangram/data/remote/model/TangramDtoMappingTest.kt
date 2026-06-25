package com.ssafy.jjongle.tangram.data.remote.model

import com.ssafy.jjongle.common.entity.AnimalType
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * TangramDtoMapping의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
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
