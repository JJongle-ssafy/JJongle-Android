package com.ssafy.jjongle.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.ssafy.jjongle.common.presentation.ui.token.DefaultArchiColor
import com.ssafy.jjongle.common.presentation.ui.theme.ArchiTheme

private val DarkColorScheme = darkColorScheme(
    primary = DefaultArchiColor.bgBrandLevel0,
    secondary = DefaultArchiColor.borderAccent,
    tertiary = DefaultArchiColor.contentAccent,
    background = DefaultArchiColor.bgDefaultLevel0,
    surface = DefaultArchiColor.bgDefaultLevel0,
    onPrimary = DefaultArchiColor.contentOnBrand,
    onSecondary = DefaultArchiColor.contentOnBrand,
    onTertiary = DefaultArchiColor.contentDefaultLevel0,
    onBackground = DefaultArchiColor.contentDefaultLevel0,
    onSurface = DefaultArchiColor.contentDefaultLevel0,
    error = DefaultArchiColor.contentDanger,
)

private val LightColorScheme = lightColorScheme(

/**
 * 메인 기능 UI에서 공유하는 Jjongle Theme 디자인 기준입니다.
 *
 * 화면별 색상, 타이포그래피, 크기 값을 직접 흩뿌리지 않고 공통 토큰을 통해 일관되게 사용합니다.
 */
    primary = DefaultArchiColor.bgBrandLevel0,
    secondary = DefaultArchiColor.borderAccent,
    tertiary = DefaultArchiColor.contentAccent,
    background = DefaultArchiColor.bgDefaultLevel0,
    surface = DefaultArchiColor.bgDefaultLevel0,
    onPrimary = DefaultArchiColor.contentOnBrand,
    onSecondary = DefaultArchiColor.contentOnBrand,
    onTertiary = DefaultArchiColor.contentDefaultLevel0,
    onBackground = DefaultArchiColor.contentDefaultLevel0,
    onSurface = DefaultArchiColor.contentDefaultLevel0,
    error = DefaultArchiColor.contentDanger,
)

@Composable
fun JjongleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    ArchiTheme {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
