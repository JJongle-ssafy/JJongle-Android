package com.ssafy.jjongle.common.domain.repository

import com.ssafy.jjongle.common.entity.AuthState

/**
 * AuthRepository domain 계층이 의존하는 저장소 계약입니다.
 *
 * - 계층: common/domain
 * - 책임: data 구현을 숨기고 유스케이스에 필요한 작업만 노출합니다.
 */
interface AuthRepository {
    suspend fun login(idToken: String): AuthState
    suspend fun signup(idToken: String, nickname: String, profileImage: String): AuthState
    suspend fun updateProfile(nickname: String, profileImage: String)
    suspend fun withdraw()
    suspend fun logout()
    suspend fun checkAuthStatus(): AuthState
}
