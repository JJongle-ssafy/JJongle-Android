package com.ssafy.jjongle.common.presentation.ui.color

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * ArchiSemanticColors Compose UI를 구성합니다.
 *
 * - 계층: common/presentation
 * - 책임: 상태를 표시하고 사용자 이벤트를 상위 콜백이나 ViewModel로 전달합니다.
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
