package com.ssafy.jjongle.common.entity

data class GoogleUser(
    val id: String,
    val email: String?,
    val displayName: String?,
    val idToken: String?,
//    val photoUrl: String? = null,
)