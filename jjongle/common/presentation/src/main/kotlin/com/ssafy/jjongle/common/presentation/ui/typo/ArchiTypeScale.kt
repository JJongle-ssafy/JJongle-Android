package com.ssafy.jjongle.common.presentation.ui.typo

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle

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
