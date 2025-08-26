package com.ssafy.jjongle.data.remote.model

//	서버에서 내려주는 응답 DTO (UserInfoDto)
data class UserInfoResponse(
    val userId: Long,
    val email: String,
    val nickname: String,
    val profileImage: String
)
