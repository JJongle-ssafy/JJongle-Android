package com.ssafy.jjongle.common.presentation.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.ssafy.jjongle.common.presentation.ui.theme.ArchiThemeImpl

/**
 * Archi Text는 공통에서 반복되는 계산이나 변환을 담당합니다.
 *
 * 호출부가 세부 구현을 직접 갖지 않도록 작은 공개 함수/값으로 분리합니다.
 */
@Composable
fun ArchiText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = ArchiThemeImpl.typeScale.textRegularM,
    color: Color = ArchiThemeImpl.archiColor.contentDefaultLevel0,
    textAlign: TextAlign? = null,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        text = text,
        modifier = modifier,
        style = style,
        color = color,
        textAlign = textAlign,
        softWrap = softWrap,
        maxLines = maxLines,
    )
}
