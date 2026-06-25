package com.ssafy.jjongle.common.entity

import com.ssafy.jjongle.common.entity.UserInfo

// Firebase Auth + Firestore profile state.

/**
 * AuthState 앱 내부에서 공유하는 도메인 값을 표현합니다.
 *
 * - 계층: common/entity
 * - 책임: 불변 값과 도메인 의미를 계층 사이에 전달합니다.
 */
data class AuthState(
    val isAuthenticated: Boolean = false,
    val user: UserInfo? = null, // firebase auth user info
    val isLoading: Boolean = false,
    val error: String? = null
)
