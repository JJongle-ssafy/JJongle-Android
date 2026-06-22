package com.ssafy.jjongle.data.firebase

data class FirebaseAuthenticatedUser(
    val uid: String,
    val email: String?,
    val displayName: String?
)
