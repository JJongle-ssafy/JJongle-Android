package com.ssafy.jjongle.common.entity

//	앱 내부에서 사용하는 도메인 유저 정보
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
