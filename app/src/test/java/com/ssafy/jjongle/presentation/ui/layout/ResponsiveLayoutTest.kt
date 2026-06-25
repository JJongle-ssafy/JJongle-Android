package com.ssafy.jjongle.common.presentation.ui.layout

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Responsive Layout Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class ResponsiveLayoutTest {

    @Test
    fun `design canvas keeps tablet reference at full scale`() {
        val metrics = calculateDesignCanvasMetrics(
            containerWidthDp = 1280f,
            containerHeightDp = 800f
        )

        assertEquals(1f, metrics.scale, 0.001f)
        assertEquals(1280f, metrics.canvasWidthDp, 0.001f)
        assertEquals(800f, metrics.canvasHeightDp, 0.001f)
        assertEquals(0f, metrics.horizontalLetterboxDp, 0.001f)
        assertEquals(0f, metrics.verticalLetterboxDp, 0.001f)
    }

    @Test
    fun `design canvas scales uniformly on landscape phone`() {
        val metrics = calculateDesignCanvasMetrics(
            containerWidthDp = 640f,
            containerHeightDp = 360f
        )

        assertEquals(0.45f, metrics.scale, 0.001f)
        assertEquals(576f, metrics.canvasWidthDp, 0.001f)
        assertEquals(360f, metrics.canvasHeightDp, 0.001f)
        assertEquals(32f, metrics.horizontalLetterboxDp, 0.001f)
        assertEquals(0f, metrics.verticalLetterboxDp, 0.001f)
    }

    @Test
    fun `design canvas scales uniformly on tall screen`() {
        val metrics = calculateDesignCanvasMetrics(
            containerWidthDp = 800f,
            containerHeightDp = 1280f
        )

        assertEquals(0.625f, metrics.scale, 0.001f)
        assertEquals(800f, metrics.canvasWidthDp, 0.001f)
        assertEquals(500f, metrics.canvasHeightDp, 0.001f)
        assertEquals(0f, metrics.horizontalLetterboxDp, 0.001f)
        assertEquals(390f, metrics.verticalLetterboxDp, 0.001f)
    }
}
