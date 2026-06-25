package com.ssafy.jjongle.common.presentation.ui.layout

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Background Layout Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class BackgroundLayoutTest {

    @Test
    fun `fill bounds layout stretches coordinates independently`() {
        val layout = calculateFillBoundsBackgroundLayout(
            containerWidth = 360f,
            containerHeight = 640f,
            imageWidth = 2800f,
            imageHeight = 1752f
        )

        assertEquals(360f / 2800f, layout.scale, 0.001f)
        assertEquals(640f / 1752f, layout.scaleY, 0.001f)
        assertEquals(180f, layout.x(1400f), 0.001f)
        assertEquals(320f, layout.y(876f), 0.001f)
        assertEquals(180f - (380f * (360f / 2800f)) / 2f, layout.leftForCenter(1400f, 380f), 0.001f)
    }
}
