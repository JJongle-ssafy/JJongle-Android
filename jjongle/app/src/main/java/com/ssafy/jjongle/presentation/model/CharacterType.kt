package com.ssafy.jjongle.presentation.model

import androidx.annotation.DrawableRes
import com.ssafy.jjongle.R

enum class CharacterType(
    val displayName: String,
    @DrawableRes val profileImageRes: Int,
    val serverName: String, // ✅ 서버 전송용 enum 값
    val lottieAsset: String? = null  // Lottie 파일은 선택 사항 (현재는 mongi만 lottie 애니메이션 사용됨)
) {
    // TODO: 추후 캐릭터 별 lottie 모션 추가 시 여기에 추가
    MONGI("원숭이", R.drawable.profile_mongi, "MONGI", "mongi_walk.json"),
    TOBY("코끼리", R.drawable.profile_toby, "TOBY"),
    LUNA("토끼", R.drawable.profile_luna, "LUNA"), ;

    companion object {
        // Kotlin 1.8 호환: entries 대신 values() 사용
        fun fromServerName(name: String?): CharacterType =
            values().firstOrNull { it.serverName == name } ?: MONGI
    }

}
