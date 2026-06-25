package com.ssafy.jjongle.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.ssafy.jjongle.common.presentation.ui.token.DefaultArchiStaticTypeScale
import com.ssafy.jjongle.main.presentation.R


// 기본 Jalnan 폰트 설정

/**
 * JalnanFont Compose UI를 구성합니다.
 *
 * - 계층: main/presentation
 * - 책임: 상태를 표시하고 사용자 이벤트를 상위 콜백이나 ViewModel로 전달합니다.
 */
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
    bodyLarge = DefaultArchiStaticTypeScale.textRegularM.copy(
        fontFamily = JalnanFont,
        fontWeight = FontWeight.Normal,
    ),
    titleLarge = DefaultArchiStaticTypeScale.textStrongL.copy(
        fontFamily = AllimjangFont, // 제목에 학교안심 알림장 사용
        fontWeight = FontWeight.Bold,
    ),
    bodyMedium = DefaultArchiStaticTypeScale.textRegularS.copy(
        fontFamily = AllimjangFont, // 필요 시 다른 스타일에도 매핑
        fontWeight = FontWeight.Normal,
    )
)
