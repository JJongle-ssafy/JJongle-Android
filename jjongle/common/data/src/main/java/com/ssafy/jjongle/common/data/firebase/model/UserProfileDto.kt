package com.ssafy.jjongle.common.data.firebase.model

import com.ssafy.jjongle.common.entity.UserInfo

data class UserProfileDto(
    val nickname: String? = null,
    val profileImage: String? = null,
    val email: String? = null
)

fun UserProfileDto.toVO(
    fallbackEmail: String?,
    fallbackDisplayName: String?
): UserInfo {
    return UserInfo(
        userId = 0L,
        email = email?.takeIf { it.isNotBlank() }
            ?: fallbackEmail?.takeIf { it.isNotBlank() }
            ?: UserInfo.MISSING_EMAIL,
        nickname = nickname?.takeIf { it.isNotBlank() }
            ?: fallbackDisplayName?.takeIf { it.isNotBlank() }
            ?: UserInfo.MISSING_NICKNAME,
        profileImage = profileImage?.takeIf { it.isNotBlank() }
            ?: UserInfo.MISSING_PROFILE_IMAGE
    )
}
