package com.ssafy.jjongle.common.presentation.ui.color

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * 공통 기반 UI에서 공유하는 Archi Semantic Colors 디자인 기준입니다.
 *
 * 화면별 색상, 타이포그래피, 크기 값을 직접 흩뿌리지 않고 공통 토큰을 통해 일관되게 사용합니다.
 */
@Immutable
data class ArchiSemanticColors(
    val bgDefaultLevel0: Color,
    val bgDefaultLevel1: Color,
    val bgBrandLevel0: Color,
    val borderDefaultLevel0: Color,
    val borderAccent: Color,
    val contentDefaultLevel0: Color,
    val contentDefaultLevel1: Color,
    val contentOnBrand: Color,
    val contentAccent: Color,
    val contentDanger: Color,
) {
    fun withStringKey(key: String): Color = when (key) {
        "bg/default/level0" -> bgDefaultLevel0
        "bg/default/level1" -> bgDefaultLevel1
        "bg/brand/level0" -> bgBrandLevel0
        "border/default/level0" -> borderDefaultLevel0
        "border/accent" -> borderAccent
        "content/default/level0" -> contentDefaultLevel0
        "content/default/level1" -> contentDefaultLevel1
        "content/on-brand" -> contentOnBrand
        "content/accent" -> contentAccent
        "content/danger" -> contentDanger
        else -> error("Unknown ArchiSemanticColors key: $key")
    }
}
