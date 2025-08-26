package com.ssafy.jjongle.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ssafy.jjongle.R


// 기본 Jalnan 폰트 설정
val JalnanFont = FontFamily(
    Font(R.font.jalnan2)
)

// 학교안심 알림장 폰트
val AllimjangFont = FontFamily(
    Font(R.font.hakgyoansim_allimjang_r, FontWeight.Normal),
    Font(R.font.hakgyoansim_allimjang_b, FontWeight.Bold)
)

// Typography 설정
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = JalnanFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = AllimjangFont, // 제목에 학교안심 알림장 사용
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = AllimjangFont, // 필요 시 다른 스타일에도 매핑
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
)