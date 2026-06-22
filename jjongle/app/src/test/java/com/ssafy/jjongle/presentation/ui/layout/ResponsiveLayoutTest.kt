package com.ssafy.jjongle.presentation.ui.layout

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResponsiveLayoutTest {

    @Test
    fun `compact ui scale keeps tablet at full size`() {
        val scale = calculateCompactUiScale(widthDp = 1280f, heightDp = 800f)

        assertEquals(1f, scale, 0.001f)
    }

    @Test
    fun `compact ui scale clamps phone to minimum readable size`() {
        val scale = calculateCompactUiScale(widthDp = 640f, heightDp = 360f)

        assertEquals(0.72f, scale, 0.001f)
    }

    @Test
    fun `mypage metrics stay within compact phone height`() {
        val metrics = calculateMypageLayoutMetrics(containerWidthDp = 640f, containerHeightDp = 360f)

        val contentHeight = metrics.contentTopPadding +
            metrics.profileFrameSize +
            metrics.profileNameGap +
            metrics.nameTextSize +
            metrics.sectionGap +
            metrics.actionRowHeight +
            metrics.contentBottomPadding

        assertTrue(contentHeight < 360f)
        assertTrue(metrics.bookButtonSize < 120f)
        assertTrue(metrics.profileFrameSize < 120f)
    }

    @Test
    fun `mypage metrics use more space on tablet than phone`() {
        val phoneMetrics = calculateMypageLayoutMetrics(containerWidthDp = 640f, containerHeightDp = 360f)
        val tabletMetrics = calculateMypageLayoutMetrics(containerWidthDp = 1280f, containerHeightDp = 800f)

        assertTrue(tabletMetrics.bookButtonSize > phoneMetrics.bookButtonSize)
        assertTrue(tabletMetrics.profileFrameSize > phoneMetrics.profileFrameSize)
        assertTrue(tabletMetrics.nameTextSize > phoneMetrics.nameTextSize)
    }
}
