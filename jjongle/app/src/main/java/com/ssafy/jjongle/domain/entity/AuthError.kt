package com.ssafy.jjongle.domain.entity

sealed class AuthError {
    object UserAlreadyExists : AuthError()
    object MissingToken : AuthError()
    object InvalidRefreshToken : AuthError()
    data class Http(val code: Int, val responseMessage: String?) : AuthError()
    data class Unknown(val detail: String?) : AuthError()
}

class AuthException(
    val error: AuthError,
    override val message: String,
    cause: Throwable? = null
) : Exception(message, cause)
