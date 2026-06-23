package com.ssafy.jjongle.presentation.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlin.math.min

const val DESIGN_CANVAS_WIDTH_DP = 1280f
const val DESIGN_CANVAS_HEIGHT_DP = 800f

val LocalLetterboxImageResController = androidx.compose.runtime.compositionLocalOf<MutableState<Int?>?> { null }

@Composable
fun SystemBackgroundImageEffect(imageRes: Int?) {
    val controller = LocalLetterboxImageResController.current
    DisposableEffect(imageRes, controller) {
        controller?.value = imageRes
        onDispose {
            if (controller?.value == imageRes) {
                controller?.value = null
            }
        }
    }
}

data class DesignCanvasMetrics(
    val scale: Float,
    val canvasWidthDp: Float,
    val canvasHeightDp: Float,
    val horizontalLetterboxDp: Float,
    val verticalLetterboxDp: Float
)

fun calculateDesignCanvasMetrics(
    containerWidthDp: Float,
    containerHeightDp: Float,
    designWidthDp: Float = DESIGN_CANVAS_WIDTH_DP,
    designHeightDp: Float = DESIGN_CANVAS_HEIGHT_DP
): DesignCanvasMetrics {
    require(containerWidthDp > 0f) { "containerWidthDp must be greater than 0." }
    require(containerHeightDp > 0f) { "containerHeightDp must be greater than 0." }
    require(designWidthDp > 0f) { "designWidthDp must be greater than 0." }
    require(designHeightDp > 0f) { "designHeightDp must be greater than 0." }

    val scale = min(containerWidthDp / designWidthDp, containerHeightDp / designHeightDp)
    val canvasWidthDp = designWidthDp * scale
    val canvasHeightDp = designHeightDp * scale

    return DesignCanvasMetrics(
        scale = scale,
        canvasWidthDp = canvasWidthDp,
        canvasHeightDp = canvasHeightDp,
        horizontalLetterboxDp = (containerWidthDp - canvasWidthDp) / 2f,
        verticalLetterboxDp = (containerHeightDp - canvasHeightDp) / 2f
    )
}

@Composable
fun DesignCanvas(
    modifier: Modifier = Modifier,
    designSize: DpSize = DpSize(DESIGN_CANVAS_WIDTH_DP.dp, DESIGN_CANVAS_HEIGHT_DP.dp),
    letterboxColor: Color = Color.Black,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(
        modifier = modifier.background(letterboxColor),
        contentAlignment = Alignment.Center
    ) {
        val bgRes = LocalLetterboxImageResController.current?.value
        if (bgRes != null) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = bgRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        val parentDensity = LocalDensity.current
        val metrics = remember(maxWidth, maxHeight, designSize) {
            calculateDesignCanvasMetrics(
                containerWidthDp = maxWidth.value,
                containerHeightDp = maxHeight.value,
                designWidthDp = designSize.width.value,
                designHeightDp = designSize.height.value
            )
        }
        val scaledDensity = remember(parentDensity, metrics.scale) {
            Density(
                density = parentDensity.density * metrics.scale,
                fontScale = parentDensity.fontScale
            )
        }

        Box(
            modifier = Modifier.size(
                width = metrics.canvasWidthDp.dp,
                height = metrics.canvasHeightDp.dp
            ),
            contentAlignment = Alignment.TopStart
        ) {
            CompositionLocalProvider(LocalDensity provides scaledDensity) {
                Box(modifier = Modifier.requiredSize(designSize.width, designSize.height)) {
                    content()
                }
            }
        }
    }
}
