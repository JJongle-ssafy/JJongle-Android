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
    fun `centered overlay returns top left position`() {
        val layout = calculateCropBackgroundLayout(
            containerWidth = 1280f,
            containerHeight = 800f,
            imageWidth = 2800f,
            imageHeight = 1752f
        )

        assertEquals(552f, layout.leftForCenter(centerX = 1400f, width = 385f), 0.001f)
        assertEquals(470.181f, layout.topForCenter(centerY = 1222.02f, height = 385f), 0.001f)
    }
}
