package com.ssafy.jjongle.domain.entity

//	앱 내부에서 사용하는 도메인 유저 정보
data class UserInfo(
    val userId: Long,
    val email: String? = null,
    val nickname: String,
    val profileImage: String
)