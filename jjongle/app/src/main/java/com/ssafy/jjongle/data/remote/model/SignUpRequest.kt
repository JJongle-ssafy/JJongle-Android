package com.ssafy.jjongle.data.remote.model

// /auth/signup 요청 바디
data class SignUpRequest(
    val firebaseIdToken: String? = null,
    val nickname: String? = null,
    val profileImage: String? = null
)
