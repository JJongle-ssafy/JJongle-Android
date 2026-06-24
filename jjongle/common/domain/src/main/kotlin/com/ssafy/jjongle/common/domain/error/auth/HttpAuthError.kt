package com.ssafy.jjongle.common.domain.error.auth

data class HttpAuthError(
    val code: Int,
    val responseMessage: String?
) : AuthError
