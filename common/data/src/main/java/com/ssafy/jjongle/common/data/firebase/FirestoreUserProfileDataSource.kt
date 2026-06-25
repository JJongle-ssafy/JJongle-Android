package com.ssafy.jjongle.common.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.ssafy.jjongle.common.data.firebase.model.UserProfileDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Firestore User Profile 데이터를 외부 서비스나 로컬 저장소에서 읽고 쓰는 data 계층 경계입니다.
 *
 * Repository가 세부 API, SDK, 저장 방식에 직접 묶이지 않도록 데이터 접근 작업을 캡슐화합니다.
 */
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
