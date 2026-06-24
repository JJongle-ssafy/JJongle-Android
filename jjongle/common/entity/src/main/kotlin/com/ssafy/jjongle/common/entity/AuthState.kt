package com.ssafy.jjongle.common.entity

import com.ssafy.jjongle.common.entity.UserInfo

// Firebase Auth + Firestore profile state.
data class AuthState(
    val isAuthenticated: Boolean = false,
    val user: UserInfo? = null, // firebase auth user info
    val isLoading: Boolean = false,
    val error: String? = null
)
