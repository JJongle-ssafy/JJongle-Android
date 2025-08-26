package com.ssafy.jjongle.data.remote.model

// /auth/refresh 요청 바디
data class RefreshRequest(
    val refreshToken: String
)
