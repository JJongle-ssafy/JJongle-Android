package com.ssafy.jjongle.common.presentation.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.ssafy.jjongle.common.presentation.ui.theme.ArchiTheme
import com.ssafy.jjongle.common.presentation.ui.theme.ArchiThemeImpl

/**
 * Base Button는 공통에서 반복되는 계산이나 변환을 담당합니다.
 *
 * 호출부가 세부 구현을 직접 갖지 않도록 작은 공개 함수/값으로 분리합니다.
 */
@Composable
fun BaseButton(
    onClick: () -> Unit,
    text: String,
    fontSize: TextUnit? = null,
    textStyle: TextStyle = ArchiThemeImpl.typeScale.titleStrongM,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colors = ArchiThemeImpl.archiColor
    Button(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.bgBrandLevel0,
            contentColor = colors.contentOnBrand,
            disabledContainerColor = colors.borderDefaultLevel0,
            disabledContentColor = colors.contentOnBrand.copy(alpha = 0.6f),
        ),
        enabled = enabled,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
    ) {
        val resolvedTextStyle = fontSize?.let { textStyle.copy(fontSize = it) } ?: textStyle
        ArchiText(
            text = text,
            style = resolvedTextStyle,
            color = colors.contentOnBrand,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BaseButtonPreview() {
    ArchiTheme {
        BaseButton(
            onClick = {},
            text = "처음으로 돌아가기"
        )
    }
}
