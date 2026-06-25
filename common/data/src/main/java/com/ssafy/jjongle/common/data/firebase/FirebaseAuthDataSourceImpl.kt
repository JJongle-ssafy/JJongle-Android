package com.ssafy.jjongle.common.data.firebase

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

/**
 * Firebase Auth Data Source Impl은 외부 SDK나 platform API를 앱 내부 계약에 맞게 연결하는 구현체입니다.
 *
 * 상위 계층은 구체 구현 대신 interface에 의존하고, 이 타입이 변환과 오류 처리를 담당합니다.
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
