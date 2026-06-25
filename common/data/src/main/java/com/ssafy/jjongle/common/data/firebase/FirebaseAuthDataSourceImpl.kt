package com.ssafy.jjongle.common.data.firebase

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

/**
 * FirebaseAuthDataSourceImpl 데이터 원본 접근을 담당합니다.
 *
 * - 계층: common/data
 * - 책임: 저장소 구현이 사용할 원격 또는 로컬 데이터 작업을 캡슐화합니다.
 */
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
