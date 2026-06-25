package com.ssafy.jjongle.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.ssafy.jjongle.common.presentation.ui.token.DefaultArchiStaticTypeScale
import com.ssafy.jjongle.main.presentation.R


// 기본 Jalnan 폰트 설정

/**
 * 메인 기능 UI에서 공유하는 Jalnan Font 디자인 기준입니다.
 *
 * 화면별 색상, 타이포그래피, 크기 값을 직접 흩뿌리지 않고 공통 토큰을 통해 일관되게 사용합니다.
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
