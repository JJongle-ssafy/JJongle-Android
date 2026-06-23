package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.entity.AuthState
import com.ssafy.jjongle.common.entity.AuthTokens
import com.ssafy.jjongle.common.entity.GoogleUser
import com.ssafy.jjongle.common.entity.UserInfo
import com.ssafy.jjongle.common.domain.repository.AuthRepository
import com.ssafy.jjongle.common.domain.repository.GoogleAuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun checkAuthStatus_restoresStateFromRepositoryResult() = runTest {
        val expected = AuthState(
            isAuthenticated = true,
            accessToken = "access",
            refreshToken = "refresh",
            user = UserInfo(
                userId = 1L,
                email = "child@example.com",
                nickname = "몽이",
                profileImage = "MONGI"
            ),
            isLoading = false
        )

        val viewModel = AuthViewModel(FakeAuthRepository(checkState = expected), FakeGoogleAuthService())
        advanceUntilIdle()

        assertEquals(expected, viewModel.authState.value)
    }

    @Test
    fun login_updatesViewModelStateAndCallsSuccess() = runTest {
        val authenticated = AuthState(
            isAuthenticated = true,
            accessToken = "access",
            refreshToken = "refresh",
            user = UserInfo(
                userId = 1L,
                email = "child@example.com",
                nickname = "토비",
                profileImage = "TOBY"
            )
        )
        val viewModel = AuthViewModel(
            FakeAuthRepository(loginResult = Result.success(authenticated)),
            FakeGoogleAuthService()
        )
        advanceUntilIdle()

        var successCalled = false
        viewModel.login(
            idToken = "firebase-token",
            onSuccess = { successCalled = true },
            onNeedSignUp = {},
            onFailure = {}
        )
        advanceUntilIdle()

        assertTrue(successCalled)
        assertEquals(authenticated.copy(isLoading = false, error = null), viewModel.authState.value)
    }

    @Test
    fun login_unauthenticatedResultCallsNeedSignUp() = runTest {
        val viewModel = AuthViewModel(
            FakeAuthRepository(
                loginResult = Result.success(AuthState(isAuthenticated = false))
            ),
            FakeGoogleAuthService()
        )
        advanceUntilIdle()

        var needSignUpCalled = false
        viewModel.login(
            idToken = "firebase-token",
            onSuccess = {},
            onNeedSignUp = { needSignUpCalled = true },
            onFailure = {}
        )
        advanceUntilIdle()

        assertTrue(needSignUpCalled)
        assertFalse(viewModel.authState.value.isAuthenticated)
        assertFalse(viewModel.authState.value.isLoading)
    }

    @Test
    fun loginWithGoogleIdToken_usesFirebaseTokenForServerLogin() = runTest {
        val authenticated = AuthState(
            isAuthenticated = true,
            accessToken = "access",
            refreshToken = "refresh"
        )
        val repository = FakeAuthRepository(loginResult = Result.success(authenticated))
        val viewModel = AuthViewModel(
            repository,
            FakeGoogleAuthService(firebaseIdToken = "firebase-id-token")
        )
        advanceUntilIdle()

        var successCalled = false
        viewModel.loginWithGoogleIdToken(
            googleIdToken = "google-id-token",
            onSuccess = { successCalled = true },
            onNeedSignUp = {},
            onFailure = {}
        )
        advanceUntilIdle()

        assertTrue(successCalled)
        assertEquals("firebase-id-token", repository.loginIdToken)
        assertEquals(authenticated.copy(isLoading = false, error = null), viewModel.authState.value)
    }

    @Test
    fun updateProfile_updatesOnlyViewModelStateAfterRepositorySucceeds() = runTest {
        val initial = AuthState(
            isAuthenticated = true,
            accessToken = "access",
            refreshToken = "refresh",
            user = UserInfo(
                userId = 1L,
                email = null,
                nickname = "기존",
                profileImage = "MONGI"
            )
        )
        val repository = FakeAuthRepository(checkState = initial)
        val viewModel = AuthViewModel(repository, FakeGoogleAuthService())
        advanceUntilIdle()

        var successCalled = false
        viewModel.updateProfile(
            nickname = "새이름",
            profileImage = "LUNA",
            onSuccess = { successCalled = true },
            onFailure = {}
        )
        advanceUntilIdle()

        assertTrue(successCalled)
        assertEquals("새이름", viewModel.authState.value.user?.nickname)
        assertEquals("LUNA", viewModel.authState.value.user?.profileImage)
        assertEquals("새이름", repository.updatedNickname)
        assertEquals("LUNA", repository.updatedProfileImage)
    }

    private class FakeAuthRepository(
        private val checkState: AuthState = AuthState(isLoading = false),
        private val loginResult: Result<AuthState> = Result.success(AuthState(isLoading = false)),
        private val signupResult: Result<AuthState> = Result.success(AuthState(isLoading = false))
    ) : AuthRepository {
        var updatedNickname: String? = null
            private set
        var updatedProfileImage: String? = null
            private set
        var loginIdToken: String? = null
            private set

        override suspend fun login(idToken: String): Result<AuthState> {
            loginIdToken = idToken
            return loginResult
        }

        override suspend fun signup(
            idToken: String,
            nickname: String,
            profileImage: String
        ): Result<AuthState> = signupResult

        override suspend fun reissue(refreshToken: String): Result<AuthState> =
            Result.success(checkState)

        override suspend fun updateProfile(nickname: String, profileImage: String) {
            updatedNickname = nickname
            updatedProfileImage = profileImage
        }

        override suspend fun withdraw() = Unit

        override suspend fun logout() = Unit

        override suspend fun checkAuthStatus(): AuthState = checkState

        override fun getStoredTokens(): AuthTokens? = null

        override fun saveTokens(tokens: AuthTokens) = Unit
    }

    private class FakeGoogleAuthService(
        private val firebaseIdToken: String = "firebase-token"
    ) : GoogleAuthService {
        override suspend fun signIn(idToken: String): Result<GoogleUser> =
            Result.success(
                GoogleUser(
                    id = "google-user",
                    email = "child@example.com",
                    displayName = "쫑글",
                    idToken = firebaseIdToken
                )
            )

        override suspend fun signOut() = Unit

        override fun getCurrentUser(): Flow<GoogleUser?> = flowOf(null)

        override suspend fun isSignedIn(): Boolean = false
    }
}
