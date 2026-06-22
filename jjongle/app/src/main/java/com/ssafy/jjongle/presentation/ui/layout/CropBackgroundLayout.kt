package com.ssafy.jjongle.presentation.ui.layout

import kotlin.math.max
import kotlin.math.min

data class CropBackgroundLayout(
    val scale: Float,
    val originX: Float,
    val originY: Float
) {
    fun scale(value: Float): Float = value * scale

    fun x(imageX: Float): Float = originX + scale(imageX)

    fun y(imageY: Float): Float = originY + scale(imageY)

    fun leftForCenter(centerX: Float, width: Float): Float = x(centerX - width / 2f)

    fun topForCenter(centerY: Float, height: Float): Float = y(centerY - height / 2f)
}

fun calculateCropBackgroundLayout(
    containerWidth: Float,
    containerHeight: Float,
    imageWidth: Float,
    imageHeight: Float
): CropBackgroundLayout {
    require(containerWidth > 0f) { "containerWidth must be greater than 0." }
    require(containerHeight > 0f) { "containerHeight must be greater than 0." }
    require(imageWidth > 0f) { "imageWidth must be greater than 0." }
    require(imageHeight > 0f) { "imageHeight must be greater than 0." }

    val scale = max(containerWidth / imageWidth, containerHeight / imageHeight)
    return CropBackgroundLayout(
        scale = scale,
        originX = (containerWidth - imageWidth * scale) / 2f,
        originY = (containerHeight - imageHeight * scale) / 2f
    )
}

fun calculateFitBackgroundLayout(
    containerWidth: Float,
    containerHeight: Float,
    imageWidth: Float,
    imageHeight: Float
): CropBackgroundLayout {
    require(containerWidth > 0f) { "containerWidth must be greater than 0." }
    require(containerHeight > 0f) { "containerHeight must be greater than 0." }
    require(imageWidth > 0f) { "imageWidth must be greater than 0." }
    require(imageHeight > 0f) { "imageHeight must be greater than 0." }

    val scale = min(containerWidth / imageWidth, containerHeight / imageHeight)
    return CropBackgroundLayout(
        scale = scale,
        originX = (containerWidth - imageWidth * scale) / 2f,
        originY = (containerHeight - imageHeight * scale) / 2f
    )
}
