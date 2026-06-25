package com.ssafy.jjongle.common.domain.repository

import com.ssafy.jjongle.common.entity.GoogleUser
import kotlinx.coroutines.flow.Flow

/**
 * GoogleAuthService 외부 서비스 연동을 담당합니다.
 *
 * - 계층: common/domain
 * - 책임: 플랫폼 또는 SDK 호출을 계층 경계 안에서 캡슐화합니다.
 */
interface GoogleAuthService {
    suspend fun signIn(idToken: String): GoogleUser
    suspend fun signOut()
    fun getCurrentUser(): Flow<GoogleUser?>
    suspend fun isSignedIn(): Boolean
} 
