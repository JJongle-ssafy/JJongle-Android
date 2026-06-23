package com.ssafy.jjongle.domain.entity

data class HttpAuthError(
    val code: Int,
    val responseMessage: String?
) : AuthError
