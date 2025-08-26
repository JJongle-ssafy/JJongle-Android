package com.ssafy.jjongle.data.remote.model

// /auth/signup 요청 바디
data class SignUpRequest(
    val firebaseIdToken: String,
    val nickname: String,
    val profileImage: String
)