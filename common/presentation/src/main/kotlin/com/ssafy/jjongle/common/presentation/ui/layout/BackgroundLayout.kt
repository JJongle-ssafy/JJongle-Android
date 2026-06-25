package com.ssafy.jjongle.common.presentation.ui.layout

import kotlin.math.min

/**
 * BackgroundLayout Compose UI를 구성합니다.
 *
 * - 계층: common/presentation
 * - 책임: 상태를 표시하고 사용자 이벤트를 상위 콜백이나 ViewModel로 전달합니다.
 */
data class BackgroundLayout(
    val scale: Float,
    val originX: Float,
    val originY: Float,
    val scaleY: Float = scale
) {
    fun scale(value: Float): Float = value * min(scale, scaleY)

    fun x(imageX: Float): Float = originX + imageX * scale

    fun y(imageY: Float): Float = originY + imageY * scaleY

    fun leftForCenter(centerX: Float, width: Float): Float = x(centerX) - scale(width) / 2f

    fun topForCenter(centerY: Float, height: Float): Float = y(centerY) - scale(height) / 2f
}

fun calculateFillBoundsBackgroundLayout(
    containerWidth: Float,
    containerHeight: Float,
    imageWidth: Float,
    imageHeight: Float
): BackgroundLayout {
    require(containerWidth > 0f) { "containerWidth must be greater than 0." }
    require(containerHeight > 0f) { "containerHeight must be greater than 0." }
    require(imageWidth > 0f) { "imageWidth must be greater than 0." }
    require(imageHeight > 0f) { "imageHeight must be greater than 0." }

    return BackgroundLayout(
        scale = containerWidth / imageWidth,
        scaleY = containerHeight / imageHeight,
        originX = 0f,
        originY = 0f
    )
}
