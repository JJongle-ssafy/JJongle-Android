package com.ssafy.jjongle.presentation.model

import androidx.annotation.DrawableRes
import com.ssafy.jjongle.main.presentation.R

/**
 * 메인 기능 흐름에서 허용되는 Character Type 값의 집합입니다.
 *
 * 분기 가능한 상태나 이벤트를 타입으로 제한해 잘못된 문자열/숫자 값이 계층 사이로 전달되지 않게 합니다.
 */
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
