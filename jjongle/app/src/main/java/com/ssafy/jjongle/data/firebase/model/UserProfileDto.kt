package com.ssafy.jjongle.data.firebase.model

import com.ssafy.jjongle.domain.entity.UserInfo

data class UserProfileDto(
    val nickname: String? = null,
    val profileImage: String? = null,
    val email: String? = null
)

fun UserProfileDto.toDomain(
    fallbackEmail: String?,
    fallbackDisplayName: String?
): UserInfo {
    return UserInfo(
        userId = 0L,
        email = email ?: fallbackEmail,
        nickname = nickname?.takeIf { it.isNotBlank() }
            ?: fallbackDisplayName?.takeIf { it.isNotBlank() }
            ?: "[MISSING_FIRESTORE_FIELD:user.nickname]",
        profileImage = profileImage?.takeIf { it.isNotBlank() }
            ?: "[MISSING_FIRESTORE_FIELD:user.profileImage]"
    )
}
