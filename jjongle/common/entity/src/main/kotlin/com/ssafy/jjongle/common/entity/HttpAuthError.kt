package com.ssafy.jjongle.common.entity

data class HttpAuthError(
    val code: Int,
    val responseMessage: String?
) : AuthError
