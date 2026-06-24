package com.ssafy.jjongle.common.domain.error.auth

class AuthException(
    val error: AuthError,
    override val message: String,
    cause: Throwable? = null
) : Exception(message, cause)
