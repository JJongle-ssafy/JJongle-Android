package com.ssafy.jjongle.common.data.repository

import com.ssafy.jjongle.common.data.firebase.FirebaseAuthenticatedUser
import com.ssafy.jjongle.common.data.firebase.FirebaseAuthDataSource
import com.ssafy.jjongle.common.data.firebase.UserProfileDataSource
import com.ssafy.jjongle.common.data.firebase.model.UserProfileDto
import com.ssafy.jjongle.common.data.firebase.model.toVO
import com.ssafy.jjongle.common.data.local.AuthDataSource
import com.ssafy.jjongle.common.domain.error.auth.AuthException
import com.ssafy.jjongle.common.entity.AuthState
import com.ssafy.jjongle.common.domain.error.auth.MissingTokenAuthError
import com.ssafy.jjongle.common.domain.error.auth.UserAlreadyExistsAuthError
import com.ssafy.jjongle.common.entity.UserInfo
import com.ssafy.jjongle.common.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val userProfileDataSource: UserProfileDataSource,
    private val authDataSource: AuthDataSource
) : AuthRepository {

    override suspend fun login(idToken: String): AuthState {
        if (idToken.isBlank()) {
            throw AuthException(MissingTokenAuthError, "Firebase ID 토큰이 비어 있습니다.")
        }

        val firebaseUser = requireCurrentFirebaseUser("로그인")
        val profile = userProfileDataSource.getProfile(firebaseUser.uid)
            ?: return AuthState(isAuthenticated = false, isLoading = false)

        return profile.toAuthenticatedState(firebaseUser)
    }

    override suspend fun signup(
        idToken: String,
        nickname: String,
        profileImage: String
    ): AuthState {
        if (idToken.isBlank()) {
            throw AuthException(MissingTokenAuthError, "Firebase ID 토큰이 비어 있습니다.")
        }

        val firebaseUser = requireCurrentFirebaseUser("회원가입")
        val existingProfile = userProfileDataSource.getProfile(firebaseUser.uid)
        if (existingProfile != null) {
            throw AuthException(UserAlreadyExistsAuthError, "이미 가입된 사용자입니다.")
        }

        val profile = UserProfileDto(
            nickname = nickname,
            profileImage = profileImage,
            email = firebaseUser.email
        )
        userProfileDataSource.saveProfile(firebaseUser.uid, profile)

        return profile.toAuthenticatedState(firebaseUser)
    }

    override suspend fun updateProfile(nickname: String, profileImage: String) {
        val firebaseUser = requireCurrentFirebaseUser("회원정보 수정")
        val existingProfile = userProfileDataSource.getProfile(firebaseUser.uid)
        val profile = UserProfileDto(
            nickname = nickname,
            profileImage = profileImage,
            email = existingProfile?.email ?: firebaseUser.email
        )

        userProfileDataSource.saveProfile(firebaseUser.uid, profile)
        authDataSource.saveUserId(firebaseUser.uid)
        authDataSource.saveUserProfile(nickname, profileImage)
    }

    override suspend fun withdraw() {
        val firebaseUser = requireCurrentFirebaseUser("회원 탈퇴")
        userProfileDataSource.deleteProfile(firebaseUser.uid)
        authDataSource.clearAuthData()
        firebaseAuthDataSource.signOut()
    }

    override suspend fun logout() {
        authDataSource.clearAuthData()
        firebaseAuthDataSource.signOut()
    }

    override suspend fun checkAuthStatus(): AuthState {
        val firebaseUser = firebaseAuthDataSource.getCurrentUser()
        if (firebaseUser == null) {
            authDataSource.clearAuthData()
            return AuthState(isAuthenticated = false, isLoading = false)
        }

        val profile = userProfileDataSource.getProfile(firebaseUser.uid)
        return if (profile == null) {
            AuthState(isAuthenticated = false, isLoading = false)
        } else {
            profile.toAuthenticatedState(firebaseUser)
        }
    }

    private fun UserProfileDto.toAuthenticatedState(firebaseUser: FirebaseAuthenticatedUser): AuthState {
        val user = toVO(
            fallbackEmail = firebaseUser.email,
            fallbackDisplayName = firebaseUser.displayName
        )
        persistUserCache(firebaseUser.uid, user)
        return AuthState(
            isAuthenticated = true,
            user = user,
            isLoading = false
        )
    }

    private fun persistUserCache(uid: String, user: UserInfo) {
        authDataSource.saveUserId(uid)
        authDataSource.saveUserProfile(user.nickname, user.profileImage)
    }

    private fun requireCurrentFirebaseUser(action: String): FirebaseAuthenticatedUser {
        return firebaseAuthDataSource.getCurrentUser()
            ?: throw AuthException(MissingTokenAuthError, "$action 가능한 Firebase 사용자가 없습니다.")
    }
}
