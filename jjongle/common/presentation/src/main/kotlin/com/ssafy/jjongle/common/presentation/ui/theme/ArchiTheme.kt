package com.ssafy.jjongle.common.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.ssafy.jjongle.common.presentation.ui.color.ArchiSemanticColors
import com.ssafy.jjongle.common.presentation.ui.token.DefaultArchiColor
import com.ssafy.jjongle.common.presentation.ui.token.DefaultArchiStaticTypeScale
import com.ssafy.jjongle.common.presentation.ui.typo.ArchiTypeScale

private val LocalArchiColor = staticCompositionLocalOf { DefaultArchiColor }
private val LocalArchiTypeScale = staticCompositionLocalOf { DefaultArchiStaticTypeScale }

object ArchiThemeImpl {
    val archiColor: ArchiSemanticColors
        @Composable
        @ReadOnlyComposable
        get() = LocalArchiColor.current

    val typeScale: ArchiTypeScale
        @Composable
        @ReadOnlyComposable
        get() = LocalArchiTypeScale.current
}

@Composable
fun ArchiTheme(
    archiColor: ArchiSemanticColors = DefaultArchiColor,
    typeScale: ArchiTypeScale = DefaultArchiStaticTypeScale,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalArchiColor provides archiColor,
        LocalArchiTypeScale provides typeScale,
    ) {
        MaterialTheme(
            colorScheme = lightColorScheme(
                primary = archiColor.bgBrandLevel0,
                onPrimary = archiColor.contentOnBrand,
                background = archiColor.bgDefaultLevel0,
                onBackground = archiColor.contentDefaultLevel0,
                surface = archiColor.bgDefaultLevel0,
                onSurface = archiColor.contentDefaultLevel0,
                error = archiColor.contentDanger,
            ),
            content = content,
        )
    }
}
