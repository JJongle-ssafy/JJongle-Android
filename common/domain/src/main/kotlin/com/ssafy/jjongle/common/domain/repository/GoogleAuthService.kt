package com.ssafy.jjongle.common.domain.repository

import com.ssafy.jjongle.common.entity.GoogleUser
import kotlinx.coroutines.flow.Flow

/**
 * Google Auth Service는 외부 인증이나 platform 기능을 앱에서 사용할 수 있게 하는 계약입니다.
 *
 * 구체 SDK 호출을 숨겨 domain/usecase가 테스트 가능한 경계에 의존하도록 합니다.
 */
interface GoogleAuthService {
    suspend fun signIn(idToken: String): GoogleUser
    suspend fun signOut()
    fun getCurrentUser(): Flow<GoogleUser?>
    suspend fun isSignedIn(): Boolean
} 
