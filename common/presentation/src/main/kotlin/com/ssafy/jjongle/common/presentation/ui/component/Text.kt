package com.ssafy.jjongle.common.presentation.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.ssafy.jjongle.common.presentation.ui.theme.ArchiThemeImpl

/**
 * ArchiText Compose UI를 구성합니다.
 *
 * - 계층: common/presentation
 * - 책임: 상태를 표시하고 사용자 이벤트를 상위 콜백이나 ViewModel로 전달합니다.
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
