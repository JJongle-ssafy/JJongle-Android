package com.ssafy.jjongle.oxgame.presentation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.ssafy.jjongle.common.presentation.ui.component.ArchiText
import com.ssafy.jjongle.common.presentation.ui.theme.ArchiThemeImpl

/**
 * OX 기능 화면의 공통 배경 이미지를 지정된 ContentScale로 채우는 Compose helper입니다.
 *
 * 타이틀, 튜토리얼, 게임 화면이 같은 방식으로 배경을 그리도록 중복 코드를 줄입니다.
 */
@Composable
internal fun OXFeatureBackgroundImage(imageRes: Int) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
internal fun OXFeatureButton(
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
            disabledContentColor = colors.contentOnBrand.copy(alpha = 0.6f)
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
