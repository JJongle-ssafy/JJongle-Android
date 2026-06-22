package com.ssafy.jjongle.presentation.ui.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import kotlin.math.min

fun calculateCompactUiScale(
    widthDp: Float,
    heightDp: Float,
    baselineShortSideDp: Float = 800f,
    minScale: Float = 0.72f,
    maxScale: Float = 1f
): Float {
    require(widthDp > 0f) { "widthDp must be greater than 0." }
    require(heightDp > 0f) { "heightDp must be greater than 0." }
    require(baselineShortSideDp > 0f) { "baselineShortSideDp must be greater than 0." }
    require(minScale > 0f) { "minScale must be greater than 0." }
    require(maxScale >= minScale) { "maxScale must be greater than or equal to minScale." }

    return (min(widthDp, heightDp) / baselineShortSideDp).coerceIn(minScale, maxScale)
}

@Composable
fun rememberCompactUiScale(): Float {
    val configuration = LocalConfiguration.current
    return remember(configuration.screenWidthDp, configuration.screenHeightDp) {
        calculateCompactUiScale(
            widthDp = configuration.screenWidthDp.toFloat(),
            heightDp = configuration.screenHeightDp.toFloat()
        )
    }
}

data class MypageLayoutMetrics(
    val horizontalPadding: Float,
    val topPadding: Float,
    val contentTopPadding: Float,
    val contentBottomPadding: Float,
    val profileFrameSize: Float,
    val profileImageSize: Float,
    val profileNameGap: Float,
    val nameTextSize: Float,
    val sectionGap: Float,
    val actionRowHeight: Float,
    val bookButtonSize: Float,
    val settingButtonWidth: Float,
    val settingButtonHeight: Float,
    val settingTextSize: Float
)

fun calculateMypageLayoutMetrics(
    containerWidthDp: Float,
    containerHeightDp: Float
): MypageLayoutMetrics {
    require(containerWidthDp > 0f) { "containerWidthDp must be greater than 0." }
    require(containerHeightDp > 0f) { "containerHeightDp must be greater than 0." }

    val scale = calculateCompactUiScale(containerWidthDp, containerHeightDp)
    val compactCanvas = containerWidthDp < 520f || containerHeightDp < 520f
    val profileMin = if (compactCanvas) 96f else 150f
    val bookMin = if (compactCanvas) 88f else 120f
    val profileHeightRatio = if (compactCanvas) 0.28f else 0.34f
    val bookHeightRatio = if (compactCanvas) 0.26f else 0.30f

    val profileFrameSize = minOf(
        containerWidthDp * 0.23f,
        containerHeightDp * profileHeightRatio,
        320f * scale
    ).coerceIn(profileMin, 320f)

    val bookButtonSize = minOf(
        containerWidthDp * 0.24f,
        containerHeightDp * bookHeightRatio,
        260f * scale
    ).coerceIn(bookMin, 260f)

    val settingButtonHeight = minOf(70f * scale, containerHeightDp * 0.12f).coerceIn(38f, 70f)

    return MypageLayoutMetrics(
        horizontalPadding = (30f * scale).coerceIn(14f, 30f),
        topPadding = (24f * scale).coerceIn(12f, 24f),
        contentTopPadding = (24f * scale).coerceIn(12f, 24f) + (56f * scale).coerceAtLeast(40f) + (8f * scale),
        contentBottomPadding = (24f * scale).coerceIn(10f, 24f),
        profileFrameSize = profileFrameSize,
        profileImageSize = profileFrameSize * 0.70f,
        profileNameGap = (8f * scale).coerceIn(4f, 8f),
        nameTextSize = minOf(50f * scale, containerHeightDp * 0.085f).coerceIn(20f, 50f),
        sectionGap = minOf(32f * scale, containerHeightDp * 0.04f).coerceIn(8f, 32f),
        actionRowHeight = bookButtonSize + (12f * scale).coerceIn(8f, 12f),
        bookButtonSize = bookButtonSize,
        settingButtonWidth = minOf(180f * scale, containerWidthDp * 0.18f).coerceIn(92f, 180f),
        settingButtonHeight = settingButtonHeight,
        settingTextSize = minOf(26f * scale, settingButtonHeight * 0.42f).coerceIn(14f, 26f)
    )
}
