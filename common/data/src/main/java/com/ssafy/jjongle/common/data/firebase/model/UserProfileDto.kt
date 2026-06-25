package com.ssafy.jjongle.common.data.firebase.model

import com.ssafy.jjongle.common.entity.UserInfo

/**
 * User Profile 원격 응답의 필드 구조를 보존하는 DTO입니다.
 *
 * 서버 필드명, null 가능성, 페이징 형태를 data 계층 안에 가두고 repository에서 앱 내부 모델로 변환합니다.
 */
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
