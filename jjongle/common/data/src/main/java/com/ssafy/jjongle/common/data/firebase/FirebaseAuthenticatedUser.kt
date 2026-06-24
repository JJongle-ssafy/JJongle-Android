package com.ssafy.jjongle.common.data.firebase

data class FirebaseAuthenticatedUser(
    val uid: String,
    val email: String?,
    val displayName: String?
)
