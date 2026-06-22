package com.ssafy.jjongle.presentation.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.ssafy.jjongle.presentation.ui.layout.shouldPreserveFullBackgroundImage

@Composable
fun ResponsiveBackgroundImage(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    imageWidth: Float = DEFAULT_BACKGROUND_IMAGE_WIDTH,
    imageHeight: Float = DEFAULT_BACKGROUND_IMAGE_HEIGHT,
    aspectRatioTolerance: Float = DEFAULT_BACKGROUND_ASPECT_RATIO_TOLERANCE
) {
    BoxWithConstraints(modifier = modifier) {
        val preserveFullImage = shouldPreserveFullBackgroundImage(
            containerWidth = maxWidth.value,
            containerHeight = maxHeight.value,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            aspectRatioTolerance = aspectRatioTolerance
        )

        if (preserveFullImage) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Image(
                    painter = painter,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        } else {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

private const val DEFAULT_BACKGROUND_IMAGE_WIDTH = 2800f
private const val DEFAULT_BACKGROUND_IMAGE_HEIGHT = 1752f
private const val DEFAULT_BACKGROUND_ASPECT_RATIO_TOLERANCE = 0.18f
