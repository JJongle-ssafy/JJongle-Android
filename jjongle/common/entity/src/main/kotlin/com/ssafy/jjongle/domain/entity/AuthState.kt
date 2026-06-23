package com.ssafy.jjongle.domain.entity

import com.ssafy.jjongle.domain.entity.UserInfo

// 로그인 상태 + 토큰 저장 상태
data class AuthState(
    val isAuthenticated: Boolean = false,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val user: UserInfo? = null, // firebase auth user info
    val isLoading: Boolean = false,
    val error: String? = null
)