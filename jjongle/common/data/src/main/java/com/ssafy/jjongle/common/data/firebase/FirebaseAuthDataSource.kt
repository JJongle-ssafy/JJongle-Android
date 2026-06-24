package com.ssafy.jjongle.common.data.firebase

interface FirebaseAuthDataSource {
    fun getCurrentUser(): FirebaseAuthenticatedUser?
    fun signOut()
}
