package com.ssafy.jjongle.domain.entity

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String
) {
    val isValid: Boolean
        get() = accessToken.isNotBlank() && refreshToken.isNotBlank()
}
