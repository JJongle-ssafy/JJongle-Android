package com.ssafy.jjongle.common.presentation.ui.layout

import kotlin.math.min

/**
 * 원본 배경 이미지의 px 좌표를 현재 Compose 컨테이너의 dp 좌표로 변환하는 배치 기준입니다.
 *
 * 지도, 탱그램 스테이지, OX 결과 화면처럼 배경 위에 패널/캐릭터/터치 영역을 올리는 화면에서
 * 배경 이미지와 오버레이 컴포넌트가 같은 좌표계를 공유하도록 x/y 좌표와 크기 스케일을 제공합니다.
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

/**
 * 배경 이미지를 컨테이너 전체에 맞춰 채울 때 사용할 좌표 변환 값을 계산합니다.
 *
 * `imageWidth`와 `imageHeight`는 원본 배경 이미지의 기준 크기이며, 반환된 [BackgroundLayout]은
 * 그 원본 좌표를 현재 화면 크기에 맞춘 위치와 크기로 변환합니다.
 */
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
