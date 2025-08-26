package com.ssafy.jjongle.data.remote.model

// /auth/login 요청 바디
data class LogInRequest(
    val firebaseIdToken: String
)