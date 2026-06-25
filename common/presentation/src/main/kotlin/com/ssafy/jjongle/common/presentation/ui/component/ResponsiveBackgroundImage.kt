package com.ssafy.jjongle.common.presentation.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale

/**
 * Responsive Background Image는 공통에서 반복되는 계산이나 변환을 담당합니다.
 *
 * 호출부가 세부 구현을 직접 갖지 않도록 작은 공개 함수/값으로 분리합니다.
 */
@Composable
fun ResponsiveBackgroundImage(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillBounds
) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier.fillMaxSize(),
        contentScale = contentScale
    )
}
