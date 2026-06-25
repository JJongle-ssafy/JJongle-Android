package com.ssafy.jjongle.common.presentation.ui.typo

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle

/**
 * 공통 기반 UI에서 공유하는 Archi Type Scale 디자인 기준입니다.
 *
 * 화면별 색상, 타이포그래피, 크기 값을 직접 흩뿌리지 않고 공통 토큰을 통해 일관되게 사용합니다.
 */
@Immutable
data class ArchiTypeScale(
    val titleStrongL: TextStyle,
    val titleStrongM: TextStyle,
    val textStrongL: TextStyle,
    val textStrongM: TextStyle,
    val textRegularM: TextStyle,
    val textRegularS: TextStyle,
) {
    fun withStringKey(key: String): TextStyle = when (key) {
        "title/strong/L" -> titleStrongL
        "title/strong/M" -> titleStrongM
        "text/strong/L" -> textStrongL
        "text/strong/M" -> textStrongM
        "text/regular/M" -> textRegularM
        "text/regular/S" -> textRegularS
        else -> error("Unknown ArchiTypeScale key: $key")
    }
}
