package com.ssafy.jjongle.data.firebase

interface FirebaseAuthDataSource {
    fun getCurrentUser(): FirebaseAuthenticatedUser?
    fun signOut()
}
