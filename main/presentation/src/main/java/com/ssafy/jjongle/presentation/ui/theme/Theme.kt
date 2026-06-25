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
 * JjongleTheme Compose UI를 구성합니다.
 *
 * - 계층: main/presentation
 * - 책임: 상태를 표시하고 사용자 이벤트를 상위 콜백이나 ViewModel로 전달합니다.
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
