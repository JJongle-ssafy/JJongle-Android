package com.ssafy.jjongle.common.domain.usecase

import com.ssafy.jjongle.common.domain.error.HttpResponseException
import com.ssafy.jjongle.common.domain.error.HttpResponseStatus
import com.ssafy.jjongle.common.domain.helper.MessageHelper
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.helper.ResourceHelper
import com.ssafy.jjongle.tti.TTIHelper
import com.ssafy.jjongle.common.domain.repository.AuthRepository
import com.ssafy.jjongle.common.entity.AuthState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthUseCaseTest {

    @Test
    fun check_auth_status_returns_success_when_repository_returns_state() = runTest {
        val state = AuthState(isAuthenticated = true, isLoading = false)
        val useCase = useCase(repository = FakeAuthRepository(checkState = state))

        val result = useCase.checkAuthStatus()

        assertTrue(result.isSuccess)
        assertEquals(state, result.getOrThrow())
    }

    @Test
    fun check_auth_status_handles_common_http_error_and_returns_failure() = runTest {
        val messageHelper = RecordingMessageHelper()
        val exception = HttpResponseException(
            status = HttpResponseStatus.Unauthorized,
            rawCode = 401,
            errorRequestUrl = "https://example.test/auth/status",
            msg = "unauthorized",
        )
        val useCase = useCase(
            repository = FakeAuthRepository(checkError = exception),
            messageHelper = messageHelper,
        )

        val result = useCase.checkAuthStatus()

        assertTrue(result.isFailure)
        assertSame(exception, result.exceptionOrNull())
        assertEquals("세션이 만료되었습니다.", messageHelper.oneButtonDialogDesc)
    }

    @Test
    fun login_handles_common_http_error_and_returns_failure() = runTest {
        val messageHelper = RecordingMessageHelper()
        val exception = HttpResponseException(
            status = HttpResponseStatus.ServerError,
            rawCode = 500,
            errorRequestUrl = "https://example.test/auth/login",
            msg = "server error",
        )
        val useCase = useCase(
            repository = FakeAuthRepository(loginError = exception),
            messageHelper = messageHelper,
        )

        val result = useCase.login("firebase-token")

        assertTrue(result.isFailure)
        assertSame(exception, result.exceptionOrNull())
        assertEquals("잠시 후 다시 시도해주세요.", messageHelper.oneButtonDialogDesc)
    }

    @Test
    fun signup_handles_common_http_error_and_returns_failure() = runTest {
        val messageHelper = RecordingMessageHelper()
        val exception = HttpResponseException(
            status = HttpResponseStatus.NotFound,
            rawCode = 404,
            errorRequestUrl = "https://example.test/auth/signup",
            msg = "not found",
        )
        val useCase = useCase(
            repository = FakeAuthRepository(signupError = exception),
            messageHelper = messageHelper,
        )

        val result = useCase.signup(
            idToken = "firebase-token",
            nickname = "새대원",
            profileImage = "LUNA",
        )

        assertTrue(result.isFailure)
        assertSame(exception, result.exceptionOrNull())
        assertEquals("지원하지 않는 기능입니다.", messageHelper.oneButtonDialogDesc)
    }

    private fun useCase(
        repository: AuthRepository,
        messageHelper: MessageHelper = MessageHelper.NoOp,
    ): AuthUseCase = AuthUseCase(
        authRepository = repository,
        resourceHelper = object : ResourceHelper {
            override fun getString(id: Int): String = id.toString()
        },
        messageHelper = messageHelper,
        navigationHelper = NavigationHelper.NoOp,
        ttiHelper = TTIHelper.NoOp,
    )

    private class FakeAuthRepository(
        private val checkState: AuthState = AuthState(isLoading = false),
        private val checkError: Throwable? = null,
        private val loginError: Throwable? = null,
        private val signupError: Throwable? = null,
        private val loginState: AuthState = AuthState(isLoading = false),
        private val signupState: AuthState = AuthState(isLoading = false),
    ) : AuthRepository {
        override suspend fun login(idToken: String): AuthState {
            loginError?.let { throw it }
            return loginState
        }

        override suspend fun signup(
            idToken: String,
            nickname: String,
            profileImage: String,
        ): AuthState {
            signupError?.let { throw it }
            return signupState
        }

        override suspend fun updateProfile(nickname: String, profileImage: String) = Unit

        override suspend fun withdraw() = Unit

        override suspend fun logout() = Unit

        override suspend fun checkAuthStatus(): AuthState {
            checkError?.let { throw it }
            return checkState
        }
    }

    private class RecordingMessageHelper : MessageHelper {
        var oneButtonDialogDesc: String? = null

        override fun showToast(messageText: String) = Unit

        override fun showSnackBar(messageText: String) = Unit

        override fun showOneButtonDialog(
            cantIgnore: Boolean,
            descText: String,
            onClickButton: () -> Unit,
        ) {
            oneButtonDialogDesc = descText
        }

        override fun showTwoButtonDialog(
            descText: String,
            onClickPositive: () -> Unit,
            onClickNegative: () -> Unit,
        ) = Unit
    }
}
