package com.ssafy.jjongle.data.firebase

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class FirebaseAuthDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : FirebaseAuthDataSource {

    override fun getCurrentUser(): FirebaseAuthenticatedUser? {
        val user = firebaseAuth.currentUser ?: return null
        return FirebaseAuthenticatedUser(
            uid = user.uid,
            email = user.email,
            displayName = user.displayName
        )
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}
