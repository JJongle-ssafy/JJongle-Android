package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.entity.AuthState
import com.ssafy.jjongle.common.entity.GoogleUser
import com.ssafy.jjongle.common.entity.UserInfo
import com.ssafy.jjongle.common.domain.helper.MessageHelper
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.helper.ResourceHelper
import com.ssafy.jjongle.tti.TTIHelper
import com.ssafy.jjongle.common.domain.repository.AuthRepository
import com.ssafy.jjongle.common.domain.repository.GoogleAuthService
import com.ssafy.jjongle.common.domain.usecase.AuthUseCase
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

/**
 * Auth의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
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
            user = UserInfo(
                userId = 1L,
                email = "child@example.com",
                nickname = "몽이",
                profileImage = "MONGI"
            ),
            isLoading = false
        )

        val viewModel = createViewModel(FakeAuthRepository(checkState = expected), FakeGoogleAuthService())
        advanceUntilIdle()

        assertEquals(expected, viewModel.uiState.value.authState)
    }

    @Test
    fun login_updatesViewModelStateAndCallsSuccessThroughMviIntent() = runTest {
        val authenticated = AuthState(
            isAuthenticated = true,
            user = UserInfo(
                userId = 1L,
                email = "child@example.com",
                nickname = "토비",
                profileImage = "TOBY"
            )
        )
        val repository = FakeAuthRepository(loginState = authenticated)
        val viewModel = createViewModel(repository, FakeGoogleAuthService())
        advanceUntilIdle()

        var successCalled = false
        viewModel.onIntent(AuthIntent.LoginWithGoogleIdToken(
            googleIdToken = "google-id-token",
            onSuccess = { successCalled = true },
            onNeedSignUp = {},
            onFailure = {}
        ))
        advanceUntilIdle()

        assertTrue(successCalled)
        assertEquals("firebase-token", repository.loginIdToken)
        assertEquals(authenticated.copy(isLoading = false, error = null), viewModel.uiState.value.authState)
    }

    @Test
    fun login_unauthenticatedResultCallsNeedSignUpThroughMviIntent() = runTest {
        val viewModel = createViewModel(
            FakeAuthRepository(
                loginState = AuthState(isAuthenticated = false)
            ),
            FakeGoogleAuthService()
        )
        advanceUntilIdle()

        var needSignUpCalled = false
        viewModel.onIntent(AuthIntent.LoginWithGoogleIdToken(
            googleIdToken = "google-id-token",
            onSuccess = {},
            onNeedSignUp = { needSignUpCalled = true },
            onFailure = {}
        ))
        advanceUntilIdle()

        assertTrue(needSignUpCalled)
        assertFalse(viewModel.uiState.value.authState.isAuthenticated)
        assertFalse(viewModel.uiState.value.authState.isLoading)
    }

    @Test
    fun loginWithGoogleIdToken_canBeRequestedThroughMviIntent() = runTest {
        val authenticated = AuthState(
            isAuthenticated = true
        )
        val repository = FakeAuthRepository(loginState = authenticated)
        val viewModel = createViewModel(
            repository,
            FakeGoogleAuthService(firebaseIdToken = "firebase-id-token")
        )
        advanceUntilIdle()

        var successCalled = false
        viewModel.onIntent(AuthIntent.LoginWithGoogleIdToken(
            googleIdToken = "google-id-token",
            onSuccess = { successCalled = true },
            onNeedSignUp = {},
            onFailure = {}
        ))
        advanceUntilIdle()

        assertTrue(successCalled)
        assertEquals("firebase-id-token", repository.loginIdToken)
        assertEquals(authenticated.copy(isLoading = false, error = null), viewModel.uiState.value.authState)
    }

    @Test
    fun signUp_canBeRequestedThroughMviIntent() = runTest {
        val signedUp = AuthState(
            isAuthenticated = true,
            user = UserInfo(
                userId = 2L,
                email = UserInfo.MISSING_EMAIL,
                nickname = "새대원",
                profileImage = "LUNA"
            )
        )
        val repository = FakeAuthRepository(signupState = signedUp)
        val viewModel = createViewModel(repository, FakeGoogleAuthService())
        advanceUntilIdle()

        var successCalled = false
        viewModel.onIntent(AuthIntent.SignUp(
            idToken = "firebase-token",
            nickname = "새대원",
            profileImage = "LUNA",
            onSuccess = { successCalled = true },
            onFailure = {},
            onNeedLogin = {},
        ))
        advanceUntilIdle()

        assertTrue(successCalled)
        assertEquals("firebase-token", repository.signupIdToken)
        assertEquals("새대원", repository.signupNickname)
        assertEquals("LUNA", repository.signupProfileImage)
        assertEquals(signedUp.copy(isLoading = false, error = null), viewModel.uiState.value.authState)
    }

    @Test
    fun updateProfile_canBeRequestedThroughMviIntent() = runTest {
        val initial = AuthState(
            isAuthenticated = true,
            user = UserInfo(
                userId = 1L,
                email = UserInfo.MISSING_EMAIL,
                nickname = "기존",
                profileImage = "MONGI"
            )
        )
        val repository = FakeAuthRepository(checkState = initial)
        val viewModel = createViewModel(repository, FakeGoogleAuthService())
        advanceUntilIdle()

        var successCalled = false
        viewModel.onIntent(AuthIntent.UpdateProfile(
            nickname = "새이름",
            profileImage = "LUNA",
            onSuccess = { successCalled = true },
            onFailure = {}
        ))
        advanceUntilIdle()

        assertTrue(successCalled)
        assertEquals("새이름", viewModel.uiState.value.authState.user?.nickname)
        assertEquals("LUNA", viewModel.uiState.value.authState.user?.profileImage)
        assertEquals("새이름", repository.updatedNickname)
        assertEquals("LUNA", repository.updatedProfileImage)
    }

    @Test
    fun logout_canBeRequestedThroughMviIntent() = runTest {
        val repository = FakeAuthRepository(
            checkState = AuthState(
                isAuthenticated = true,
                user = UserInfo(
                    userId = 1L,
                    email = UserInfo.MISSING_EMAIL,
                    nickname = "기존",
                    profileImage = "MONGI"
                )
            )
        )
        val googleAuthService = FakeGoogleAuthService()
        val viewModel = createViewModel(repository, googleAuthService)
        advanceUntilIdle()

        viewModel.onIntent(AuthIntent.Logout)
        advanceUntilIdle()

        assertTrue(repository.logoutCalled)
        assertTrue(googleAuthService.signOutCalled)
        assertEquals(
            AuthState(isAuthenticated = false, isLoading = false),
            viewModel.uiState.value.authState
        )
    }

    @Test
    fun withdraw_canBeRequestedThroughMviIntent() = runTest {
        val repository = FakeAuthRepository(
            checkState = AuthState(
                isAuthenticated = true,
                user = UserInfo(
                    userId = 1L,
                    email = UserInfo.MISSING_EMAIL,
                    nickname = "기존",
                    profileImage = "MONGI"
                )
            )
        )
        val viewModel = createViewModel(repository, FakeGoogleAuthService())
        advanceUntilIdle()

        var successCalled = false
        viewModel.onIntent(AuthIntent.Withdraw(
            onSuccess = { successCalled = true },
            onFailure = {}
        ))
        advanceUntilIdle()

        assertTrue(successCalled)
        assertTrue(repository.withdrawCalled)
        assertEquals(
            AuthState(isAuthenticated = false, isLoading = false),
            viewModel.uiState.value.authState
        )
    }

    @Test
    fun local_auth_error_can_be_reported_through_mvi_intent() = runTest {
        val viewModel = createViewModel(FakeAuthRepository(), FakeGoogleAuthService())
        advanceUntilIdle()

        viewModel.onIntent(AuthIntent.ShowError("구글 ID 토큰을 가져오지 못했습니다."))
        advanceUntilIdle()

        assertEquals("구글 ID 토큰을 가져오지 못했습니다.", viewModel.uiState.value.authState.error)
        assertFalse(viewModel.uiState.value.authState.isLoading)
    }

    private fun createViewModel(
        repository: AuthRepository,
        googleAuthService: GoogleAuthService,
    ): AuthViewModel {
        val authUseCase = AuthUseCase(
            authRepository = repository,
            resourceHelper = object : ResourceHelper {
                override fun getString(id: Int): String = id.toString()
            },
            messageHelper = MessageHelper.NoOp,
            navigationHelper = NavigationHelper.NoOp,
            ttiHelper = TTIHelper.NoOp,
        )
        return AuthViewModel(authUseCase, googleAuthService)
    }

    private class FakeAuthRepository(
        private val checkState: AuthState = AuthState(isLoading = false),
        private val loginState: AuthState = AuthState(isLoading = false),
        private val signupState: AuthState = AuthState(isLoading = false)
    ) : AuthRepository {
        var updatedNickname: String? = null
            private set
        var updatedProfileImage: String? = null
            private set
        var loginIdToken: String? = null
            private set
        var signupIdToken: String? = null
            private set
        var signupNickname: String? = null
            private set
        var signupProfileImage: String? = null
            private set
        var logoutCalled: Boolean = false
            private set
        var withdrawCalled: Boolean = false
            private set

        override suspend fun login(idToken: String): AuthState {
            loginIdToken = idToken
            return loginState
        }

        override suspend fun signup(
            idToken: String,
            nickname: String,
            profileImage: String
        ): AuthState {
            signupIdToken = idToken
            signupNickname = nickname
            signupProfileImage = profileImage
            return signupState
        }

        override suspend fun updateProfile(nickname: String, profileImage: String) {
            updatedNickname = nickname
            updatedProfileImage = profileImage
        }

        override suspend fun withdraw() {
            withdrawCalled = true
        }

        override suspend fun logout() {
            logoutCalled = true
        }

        override suspend fun checkAuthStatus(): AuthState = checkState
    }

    private class FakeGoogleAuthService(
        private val firebaseIdToken: String = "firebase-token"
    ) : GoogleAuthService {
        var signOutCalled: Boolean = false
            private set

        override suspend fun signIn(idToken: String): GoogleUser =
            GoogleUser(
                id = "google-user",
                email = "child@example.com",
                displayName = "쫑글",
                idToken = firebaseIdToken
            )

        override suspend fun signOut() {
            signOutCalled = true
        }

        override fun getCurrentUser(): Flow<GoogleUser?> = flowOf(null)

        override suspend fun isSignedIn(): Boolean = false
    }
}
