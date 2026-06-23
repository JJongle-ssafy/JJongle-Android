package com.ssafy.jjongle.domain.entity

data class UnknownAuthError(
    val detail: String?
) : AuthError
