package com.ssafy.jjongle.domain.entity

data class GoogleUser(
    val id: String,
    val email: String?,
    val displayName: String?,
    val idToken: String?,
//    val photoUrl: String? = null,
)