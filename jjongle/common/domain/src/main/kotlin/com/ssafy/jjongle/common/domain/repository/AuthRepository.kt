package com.ssafy.jjongle.common.domain.repository

import com.ssafy.jjongle.common.entity.AuthState

interface AuthRepository {
    suspend fun login(idToken: String): AuthState
    suspend fun signup(idToken: String, nickname: String, profileImage: String): AuthState
    suspend fun updateProfile(nickname: String, profileImage: String)
    suspend fun withdraw()
    suspend fun logout()
    suspend fun checkAuthStatus(): AuthState
}
