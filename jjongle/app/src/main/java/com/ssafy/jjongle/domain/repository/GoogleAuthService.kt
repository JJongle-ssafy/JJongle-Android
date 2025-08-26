package com.ssafy.jjongle.domain.repository

import com.ssafy.jjongle.domain.entity.GoogleUser
import kotlinx.coroutines.flow.Flow

interface GoogleAuthService {
    suspend fun signIn(idToken: String): Result<GoogleUser>
    suspend fun signOut()
    fun getCurrentUser(): Flow<GoogleUser?>
    suspend fun isSignedIn(): Boolean
} 