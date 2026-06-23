package com.ssafy.jjongle.domain.entity

class AuthException(
    val error: AuthError,
    override val message: String,
    cause: Throwable? = null
) : Exception(message, cause)
