package com.ssafy.jjongle.common.data.service

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.ssafy.jjongle.common.entity.GoogleUser
import com.ssafy.jjongle.common.domain.repository.GoogleAuthService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Google Auth Service Impl은 외부 SDK나 platform API를 앱 내부 계약에 맞게 연결하는 구현체입니다.
 *
 * 상위 계층은 구체 구현 대신 interface에 의존하고, 이 타입이 변환과 오류 처리를 담당합니다.
 */
@Singleton
class GoogleAuthServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : GoogleAuthService {

    private val _currentUser = MutableStateFlow<GoogleUser?>(null)
    override fun getCurrentUser(): Flow<GoogleUser?> = _currentUser.asStateFlow()

    override suspend fun signIn(idToken: String): GoogleUser {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        val authResult = FirebaseAuth.getInstance()
            .signInWithCredential(credential)
            .await()

        val user = authResult.user
            ?: throw IllegalStateException("Firebase 사용자 정보 없음")

        val firebaseIdToken = user.getIdToken(true).await().token.orEmpty()
        if (firebaseIdToken.isBlank()) {
            throw IllegalStateException("Firebase ID token 발급 실패")
        }

        val googleUser = GoogleUser(
            id = user.uid,
            email = user.email,
            displayName = user.displayName,
            idToken = firebaseIdToken
        )

        _currentUser.value = googleUser

        return googleUser
    }

    override suspend fun signOut() {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).signOut()
        FirebaseAuth.getInstance().signOut()
        _currentUser.value = null
    }

    override suspend fun isSignedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }
}
