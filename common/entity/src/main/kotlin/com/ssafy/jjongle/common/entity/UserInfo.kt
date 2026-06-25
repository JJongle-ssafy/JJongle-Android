package com.ssafy.jjongle.common.entity

//	앱 내부에서 사용하는 도메인 유저 정보

/**
 * UserInfo 앱 내부에서 공유하는 도메인 값을 표현합니다.
 *
 * - 계층: common/entity
 * - 책임: 불변 값과 도메인 의미를 계층 사이에 전달합니다.
 */
data class UserInfo(
    val userId: Long = 0L,
    val email: String = MISSING_EMAIL,
    val nickname: String = MISSING_NICKNAME,
    val profileImage: String = MISSING_PROFILE_IMAGE,
) {
    companion object {
        const val MISSING_EMAIL = "[MISSING_FIRESTORE_FIELD:user.email]"
        const val MISSING_NICKNAME = "[MISSING_FIRESTORE_FIELD:user.nickname]"
        const val MISSING_PROFILE_IMAGE = "[MISSING_FIRESTORE_FIELD:user.profileImage]"
        val empty = UserInfo()
    }
}
