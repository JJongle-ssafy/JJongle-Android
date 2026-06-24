package com.ssafy.jjongle.common.domain.error.auth

data class UnknownAuthError(
    val detail: String?
) : AuthError
