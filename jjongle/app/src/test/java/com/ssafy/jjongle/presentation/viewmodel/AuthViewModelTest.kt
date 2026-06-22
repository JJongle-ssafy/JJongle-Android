package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.domain.entity.AuthState
import com.ssafy.jjongle.domain.entity.UserInfo
import com.ssafy.jjongle.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

        val viewModel = AuthViewModel(FakeAuthRepository(checkState = expected))
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
            FakeAuthRepository(loginResult = Result.success(authenticated))
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
            )
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
        val viewModel = AuthViewModel(repository)
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

        override suspend fun login(idToken: String): Result<AuthState> = loginResult

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
    }
}
