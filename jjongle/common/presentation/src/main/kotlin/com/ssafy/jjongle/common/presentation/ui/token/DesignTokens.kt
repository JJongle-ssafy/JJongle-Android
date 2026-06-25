package com.ssafy.jjongle.common.presentation.ui.token

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ssafy.jjongle.common.presentation.ui.color.ArchiSemanticColors
import com.ssafy.jjongle.common.presentation.ui.typo.ArchiTypeScale

/**
 * ArchiPaletteColors Compose UI를 구성합니다.
 *
 * - 계층: common/presentation
 * - 책임: 상태를 표시하고 사용자 이벤트를 상위 콜백이나 ViewModel로 전달합니다.
 */
object ArchiPaletteColors {
    val White = Color(0xFFFFFFFF)
    val Gray100 = Color(0xFFF4F0EA)
    val Gray500 = Color(0xFF8C8177)
    val Brown900 = Color(0xFF3F1E13)
    val Brown800 = Color(0xFF562405)
    val Brown700 = Color(0xFF5C3B1E)
    val Green700 = Color(0xFF567147)
    val Amber400 = Color(0xFFFBBF5E)
    val Red600 = Color(0xFFD32F2F)
}

// FIGMA-TOKEN-INJECTION-POINT: palette

val DefaultArchiColor = ArchiSemanticColors(
    bgDefaultLevel0 = ArchiPaletteColors.White,
    bgDefaultLevel1 = ArchiPaletteColors.Gray100,
    bgBrandLevel0 = ArchiPaletteColors.Brown800,
    borderDefaultLevel0 = ArchiPaletteColors.Gray500,
    borderAccent = ArchiPaletteColors.Green700,
    contentDefaultLevel0 = ArchiPaletteColors.Brown900,
    contentDefaultLevel1 = ArchiPaletteColors.Brown700,
    contentOnBrand = ArchiPaletteColors.White,
    contentAccent = ArchiPaletteColors.Amber400,
    contentDanger = ArchiPaletteColors.Red600,
)

// FIGMA-TOKEN-INJECTION-POINT: semantic-colors

val DefaultArchiStaticTypeScale = ArchiTypeScale(
    titleStrongL = TextStyle(fontSize = 42.sp, fontWeight = FontWeight.Bold),
    titleStrongM = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold),
    textStrongL = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
    textStrongM = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
    textRegularM = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
    textRegularS = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
)

// FIGMA-TOKEN-INJECTION-POINT: type-scale
