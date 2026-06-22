package com.ssafy.jjongle.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import com.ssafy.jjongle.data.firebase.FirebaseAuthenticatedUser
import com.ssafy.jjongle.data.firebase.FirebaseAuthDataSource
import com.ssafy.jjongle.data.firebase.UserProfileDataSource
import com.ssafy.jjongle.data.firebase.model.UserProfileDto
import com.ssafy.jjongle.data.firebase.model.toDomain
import com.ssafy.jjongle.data.local.AuthDataSource
import com.ssafy.jjongle.domain.entity.AuthException
import com.ssafy.jjongle.domain.entity.AuthState
import com.ssafy.jjongle.domain.entity.AuthStorageUnavailableError
import com.ssafy.jjongle.domain.entity.AuthTokens
import com.ssafy.jjongle.domain.entity.InvalidRefreshTokenAuthError
import com.ssafy.jjongle.domain.entity.MissingTokenAuthError
import com.ssafy.jjongle.domain.entity.UnknownAuthError
import com.ssafy.jjongle.domain.entity.UserAlreadyExistsAuthError
import com.ssafy.jjongle.domain.entity.UserInfo
import com.ssafy.jjongle.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val userProfileDataSource: UserProfileDataSource,
    private val authDataSource: AuthDataSource
) : AuthRepository {

    override suspend fun login(idToken: String): Result<AuthState> {
        return try {
            if (idToken.isBlank()) {
                throw AuthException(MissingTokenAuthError, "Firebase ID 토큰이 비어 있습니다.")
            }

            val firebaseUser = requireCurrentFirebaseUser("로그인")
            val profile = userProfileDataSource.getProfile(firebaseUser.uid)
                ?: return Result.success(AuthState(isAuthenticated = false, isLoading = false))

            Result.success(profile.toAuthenticatedState(firebaseUser))
        } catch (e: AuthException) {
            Log.e(TAG, "로그인 중 인증 오류 발생: ${e.message}", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "로그인 중 일반 Exception 발생", e)
            Result.failure(e.toUnknownAuthException("로그인 중 알 수 없는 오류가 발생했습니다."))
        }
    }

    override suspend fun signup(
        idToken: String,
        nickname: String,
        profileImage: String
    ): Result<AuthState> {
        return try {
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

            Result.success(profile.toAuthenticatedState(firebaseUser))
        } catch (e: AuthException) {
            Log.e(TAG, "회원가입 중 인증 오류 발생: ${e.message}", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "회원가입 중 일반 Exception 발생", e)
            Result.failure(e.toUnknownAuthException("회원가입 중 알 수 없는 오류가 발생했습니다."))
        }
    }

    @Deprecated("Legacy backend token reissue is unused in the serverless auth flow.")
    override suspend fun reissue(refreshToken: String): Result<AuthState> {
        if (refreshToken.isBlank()) {
            return Result.failure(
                AuthException(InvalidRefreshTokenAuthError, "유효한 리프레시 토큰이 없습니다.")
            )
        }
        return Result.success(checkAuthStatus())
    }

    override suspend fun updateProfile(nickname: String, profileImage: String) {
        try {
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
        } catch (e: Exception) {
            Log.e(TAG, "회원정보 수정 실패", e)
            throw e.toUnknownAuthException("회원정보 수정에 실패했습니다.")
        }
    }

    override suspend fun withdraw() {
        try {
            val firebaseUser = requireCurrentFirebaseUser("회원 탈퇴")
            userProfileDataSource.deleteProfile(firebaseUser.uid)
            authDataSource.clearAuthData()
            firebaseAuthDataSource.signOut()
        } catch (e: Exception) {
            Log.e(TAG, "회원 탈퇴 실패", e)
            throw e.toUnknownAuthException("회원 탈퇴에 실패했습니다.")
        }
    }

    override suspend fun logout() {
        authDataSource.clearAuthData()
        firebaseAuthDataSource.signOut()
        Log.d(TAG, "로그아웃 완료: Firebase 세션 및 로컬 인증 캐시 삭제")
    }

    override suspend fun checkAuthStatus(): AuthState {
        val firebaseUser = firebaseAuthDataSource.getCurrentUser()
        if (firebaseUser == null) {
            authDataSource.clearAuthData()
            Log.d(TAG, "Firebase 사용자 없음. 로그아웃 상태로 처리")
            return AuthState(isAuthenticated = false, isLoading = false)
        }

        return try {
            val profile = userProfileDataSource.getProfile(firebaseUser.uid)
            if (profile == null) {
                Log.w(TAG, "Firestore 사용자 프로필 없음. 회원가입 필요 상태로 처리")
                AuthState(isAuthenticated = false, isLoading = false)
            } else {
                profile.toAuthenticatedState(firebaseUser)
            }
        } catch (e: Exception) {
            Log.e(TAG, "로그인 상태 확인 실패", e)
            val cachedUser = getCachedUser()
            if (cachedUser == null) {
                AuthState(
                    isAuthenticated = false,
                    isLoading = false,
                    error = e.message ?: "로그인 상태 확인 실패"
                )
            } else {
                AuthState(
                    isAuthenticated = true,
                    user = cachedUser,
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    @Deprecated("Server-issued access/refresh tokens are legacy. Firebase Auth session is the active auth source.")
    override fun getStoredTokens(): AuthTokens? {
        val accessToken = authDataSource.getAccessToken()
        val refreshToken = authDataSource.getRefreshToken()
        if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) return null
        return AuthTokens(accessToken = accessToken, refreshToken = refreshToken)
    }

    @Deprecated("Server-issued access/refresh tokens are legacy. Kept for Unity/backend migration compatibility.")
    override fun saveTokens(tokens: AuthTokens) {
        if (!tokens.isValid) {
            throw AuthException(MissingTokenAuthError, "저장할 인증 토큰이 비어 있습니다.")
        }
        authDataSource.saveTokens(tokens.accessToken, tokens.refreshToken)
    }

    private fun UserProfileDto.toAuthenticatedState(firebaseUser: FirebaseAuthenticatedUser): AuthState {
        val user = toDomain(
            fallbackEmail = firebaseUser.email,
            fallbackDisplayName = firebaseUser.displayName
        )
        persistUserCache(firebaseUser.uid, user)
        return AuthState(
            isAuthenticated = true,
            accessToken = null,
            refreshToken = null,
            user = user,
            isLoading = false
        )
    }

    private fun persistUserCache(uid: String, user: UserInfo) {
        authDataSource.saveUserId(uid)
        authDataSource.saveUserProfile(user.nickname, user.profileImage)
    }

    private fun getCachedUser(): UserInfo? {
        val nickname = authDataSource.getNickname()
        val profile = authDataSource.getProfileImage()
        return if (!nickname.isNullOrBlank() && !profile.isNullOrBlank()) {
            UserInfo(userId = 0L, email = null, nickname = nickname, profileImage = profile)
        } else {
            null
        }
    }

    private fun requireCurrentFirebaseUser(action: String): FirebaseAuthenticatedUser {
        return firebaseAuthDataSource.getCurrentUser()
            ?: throw AuthException(MissingTokenAuthError, "$action 가능한 Firebase 사용자가 없습니다.")
    }

    private fun Throwable.toUnknownAuthException(defaultMessage: String): AuthException {
        return when (this) {
            is AuthException -> this
            is FirebaseFirestoreException -> toFirestoreAuthException(defaultMessage)
            else -> AuthException(UnknownAuthError(message), message ?: defaultMessage, this)
        }
    }

    private fun FirebaseFirestoreException.toFirestoreAuthException(defaultMessage: String): AuthException {
        val mappedMessage = when (code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                "Firestore 사용자 저장소에 접근할 수 없습니다. Firebase Console에서 Cloud Firestore API 활성화와 보안 규칙을 확인하세요."
            FirebaseFirestoreException.Code.UNAVAILABLE ->
                "Firestore 사용자 저장소에 연결할 수 없습니다. 네트워크 상태 또는 Firebase 프로젝트 설정을 확인하세요."
            else -> message ?: defaultMessage
        }
        return AuthException(AuthStorageUnavailableError, mappedMessage, this)
    }

    private companion object {
        const val TAG = "AuthRepositoryImpl"
    }
}
