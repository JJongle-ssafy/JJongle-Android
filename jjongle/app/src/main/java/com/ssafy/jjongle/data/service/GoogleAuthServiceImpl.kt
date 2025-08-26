package com.ssafy.jjongle.data.service

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.ssafy.jjongle.domain.entity.GoogleUser
import com.ssafy.jjongle.domain.repository.GoogleAuthService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleAuthServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : GoogleAuthService {

    private val _currentUser = MutableStateFlow<GoogleUser?>(null)
    override fun getCurrentUser(): Flow<GoogleUser?> = _currentUser.asStateFlow()

    override suspend fun signIn(idToken: String): Result<GoogleUser> {
        // [1] 로그인 진입 로그
        Log.d("GoogleAuth", "signIn() called with idToken: $idToken")

        return try {
            // [2] Credential 생성 시도
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            Log.d("GoogleAuth", "Firebase credential 생성 완료")

            // [3] Firebase 로그인 시도
            val authResult = FirebaseAuth.getInstance()
                .signInWithCredential(credential)
                .await()
            Log.d("GoogleAuth", "Firebase 로그인 완료: ${authResult.user?.uid}")

            // [4] 사용자 정보 확인
            val user = authResult.user
            if (user == null) {
                Log.e("GoogleAuth", "Firebase 사용자 정보 없음")
                return Result.failure(IllegalStateException("Firebase 사용자 정보 없음"))
            }

            // [5] GoogleUser로 wrapping
            val googleUser = GoogleUser(
                id = user.uid,
                email = user.email,
                displayName = user.displayName,
                idToken = idToken
            )

            // [6] 상태 저장
            _currentUser.value = googleUser
            Log.d("GoogleAuth", "로그인 성공, 상태 저장 완료")

            Result.success(googleUser)
        } catch (e: Exception) {
            // [7] 예외 처리 로그
            Log.e("GoogleAuth", "signIn 실패", e)
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).signOut()
        FirebaseAuth.getInstance().signOut()
        _currentUser.value = null // ✅ 상태 초기화
    }

    override suspend fun isSignedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }
}
