package com.ssafy.jjongle.common.data.firebase.model

import com.ssafy.jjongle.common.entity.UserInfo

/**
 * UserProfileDto 외부 데이터 응답을 표현하는 DTO입니다.
 *
 * - 계층: common/data
 * - 책임: data 계층에서 외부 데이터 형태를 보존하고 domain/entity 모델로 변환합니다.
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
