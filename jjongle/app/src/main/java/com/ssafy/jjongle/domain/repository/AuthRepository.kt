package com.ssafy.jjongle.domain.repository

import com.ssafy.jjongle.domain.entity.AuthState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getAuthState(): Flow<AuthState>

    suspend fun login(idToken: String): Result<AuthState>
    suspend fun signup(idToken: String, nickname: String, profileImage: String): Result<AuthState>
    suspend fun reissue(refreshToken: String): Result<AuthState>
    suspend fun updateProfile(nickname: String, profileImage: String)
    suspend fun withdraw()
    suspend fun logout()
    suspend fun checkAuthStatus()
}
