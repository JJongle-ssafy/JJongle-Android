package com.ssafy.jjongle.common.domain.repository

import com.ssafy.jjongle.common.entity.AuthState
import com.ssafy.jjongle.common.entity.AuthTokens

interface AuthRepository {
    suspend fun login(idToken: String): Result<AuthState>
    suspend fun signup(idToken: String, nickname: String, profileImage: String): Result<AuthState>
    @Deprecated("Legacy backend token reissue is unused in the Firebase/Firestore auth flow.")
    suspend fun reissue(refreshToken: String): Result<AuthState>
    suspend fun updateProfile(nickname: String, profileImage: String)
    suspend fun withdraw()
    suspend fun logout()
    suspend fun checkAuthStatus(): AuthState
    @Deprecated("Server-issued tokens are legacy. Firebase Auth session is the active auth source.")
    fun getStoredTokens(): AuthTokens?
    @Deprecated("Server-issued tokens are legacy. Kept for legacy backend compatibility.")
    fun saveTokens(tokens: AuthTokens)
}
