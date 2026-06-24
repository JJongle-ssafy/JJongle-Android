package com.ssafy.jjongle.presentation.model

import androidx.annotation.DrawableRes
import com.ssafy.jjongle.main.presentation.R

enum class CharacterType(
    val displayName: String,
    @DrawableRes val profileImageRes: Int,
    val serverName: String,
    val lottieAsset: String? = null,
) {
    MONGI("원숭이", R.drawable.profile_mongi, "MONGI", "mongi_walk.json"),
    TOBY("코끼리", R.drawable.profile_toby, "TOBY"),
    LUNA("토끼", R.drawable.profile_luna, "LUNA"), ;

    companion object {
        fun fromServerName(name: String?): CharacterType =
            values().firstOrNull { it.serverName == name } ?: MONGI
    }
}
