package com.ssafy.jjongle.common.entity

//	앱 내부에서 사용하는 도메인 유저 정보

/**
 * User Info는 공통 흐름에서 계층 사이로 전달되는 도메인 값입니다.
 *
 * 원시 값 여러 개를 그대로 넘기지 않고 이름 있는 타입으로 묶어 호출 의도를 명확히 합니다.
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
