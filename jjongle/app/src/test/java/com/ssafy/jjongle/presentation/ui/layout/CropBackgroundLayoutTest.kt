package com.ssafy.jjongle.presentation.ui.layout

import org.junit.Assert.assertEquals
import org.junit.Test

class CropBackgroundLayoutTest {

    @Test
    fun `crop layout keeps image center aligned with container center`() {
        val layout = calculateCropBackgroundLayout(
            containerWidth = 1280f,
            containerHeight = 800f,
            imageWidth = 2800f,
            imageHeight = 1752f
        )

        assertEquals(640f, layout.x(1400f), 0.001f)
        assertEquals(400f, layout.y(876f), 0.001f)
    }

    @Test
    fun `wide container crops image vertically`() {
        val layout = calculateCropBackgroundLayout(
            containerWidth = 1600f,
            containerHeight = 800f,
            imageWidth = 2800f,
            imageHeight = 1752f
        )

        assertEquals(1600f / 2800f, layout.scale, 0.001f)
        assertEquals(0f, layout.originX, 0.001f)
        assertEquals(-100.571f, layout.originY, 0.001f)
    }

    @Test
    fun `tall container crops image horizontally`() {
        val layout = calculateCropBackgroundLayout(
            containerWidth = 1000f,
            containerHeight = 1000f,
            imageWidth = 2800f,
            imageHeight = 1752f
        )

        assertEquals(1000f / 1752f, layout.scale, 0.001f)
        assertEquals(-299.087f, layout.originX, 0.001f)
        assertEquals(0f, layout.originY, 0.001f)
    }

    @Test
    fun `podium overlay returns top left position above podium`() {
        val layout = calculateFitBackgroundLayout(
            containerWidth = 1280f,
            containerHeight = 800f,
            imageWidth = 2800f,
            imageHeight = 1752f
        )

        assertEquals(553.242f, layout.leftForCenter(centerX = 1400f, width = 380f), 0.001f)
        assertEquals(196.347f, layout.topForCenter(centerY = 620f, height = 380f), 0.001f)
    }

    @Test
    fun `fit layout keeps full image visible on portrait phone`() {
        val layout = calculateFitBackgroundLayout(
            containerWidth = 360f,
            containerHeight = 640f,
            imageWidth = 2800f,
            imageHeight = 1752f
        )

        assertEquals(360f / 2800f, layout.scale, 0.001f)
        assertEquals(0f, layout.originX, 0.001f)
        assertEquals(207.371f, layout.originY, 0.001f)
    }

    @Test
    fun `adaptive layout fills tablet landscape when aspect ratio is close`() {
        val layout = calculateAdaptiveBackgroundLayout(
            containerWidth = 1280f,
            containerHeight = 800f,
            imageWidth = 2800f,
            imageHeight = 1752f
        )

        val cropLayout = calculateCropBackgroundLayout(
            containerWidth = 1280f,
            containerHeight = 800f,
            imageWidth = 2800f,
            imageHeight = 1752f
        )
        assertEquals(cropLayout.scale, layout.scale, 0.001f)
        assertEquals(cropLayout.originY, layout.originY, 0.001f)
    }

    @Test
    fun `adaptive layout preserves full image on portrait phone`() {
        val layout = calculateAdaptiveBackgroundLayout(
            containerWidth = 360f,
            containerHeight = 640f,
            imageWidth = 2800f,
            imageHeight = 1752f
        )

        val fitLayout = calculateFitBackgroundLayout(
            containerWidth = 360f,
            containerHeight = 640f,
            imageWidth = 2800f,
            imageHeight = 1752f
        )
        assertEquals(fitLayout.scale, layout.scale, 0.001f)
        assertEquals(fitLayout.originY, layout.originY, 0.001f)
    }

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
