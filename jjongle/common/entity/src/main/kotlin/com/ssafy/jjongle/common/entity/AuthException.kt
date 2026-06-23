package com.ssafy.jjongle.common.entity

class AuthException(
    val error: AuthError,
    override val message: String,
    cause: Throwable? = null
) : Exception(message, cause)
