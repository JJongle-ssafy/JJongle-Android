package com.ssafy.jjongle.common.presentation.ui.layout

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * BackgroundLayout의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
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
