package com.ssafy.jjongle.common.entity

data class UnknownAuthError(
    val detail: String?
) : AuthError
