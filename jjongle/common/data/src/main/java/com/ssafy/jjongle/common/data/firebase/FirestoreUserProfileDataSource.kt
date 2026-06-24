package com.ssafy.jjongle.common.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.ssafy.jjongle.common.data.firebase.model.UserProfileDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreUserProfileDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserProfileDataSource {

    private val users = firestore.collection(COLLECTION_USERS)

    override suspend fun getProfile(uid: String): UserProfileDto? {
        val snapshot = users.document(uid).get().await()
        if (!snapshot.exists()) return null
        return snapshot.toObject(UserProfileDto::class.java)
    }

    override suspend fun saveProfile(uid: String, profile: UserProfileDto) {
        users.document(uid).set(profile).await()
    }

    override suspend fun deleteProfile(uid: String) {
        users.document(uid).delete().await()
    }

    private companion object {
        const val COLLECTION_USERS = "users"
    }
}
