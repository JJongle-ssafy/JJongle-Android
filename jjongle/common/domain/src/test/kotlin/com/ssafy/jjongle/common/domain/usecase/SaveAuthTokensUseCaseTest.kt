package com.ssafy.jjongle.common.domain.usecase

import com.ssafy.jjongle.common.entity.AuthState
import com.ssafy.jjongle.common.entity.AuthTokens
import com.ssafy.jjongle.common.domain.repository.AuthRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveAuthTokensUseCaseTest {

    @Test
    fun invoke_savesValidTokens() {
        val repository = FakeAuthRepository()
        val useCase = SaveAuthTokensUseCase(repository)

        val result = useCase("access", "refresh")

        assertTrue(result.isSuccess)
        assertEquals(AuthTokens("access", "refresh"), repository.savedTokens)
    }

    @Test
    fun invoke_rejectsBlankTokens() {
        val repository = FakeAuthRepository()
        val useCase = SaveAuthTokensUseCase(repository)

        val result = useCase("", "refresh")

        assertTrue(result.isFailure)
        assertEquals(null, repository.savedTokens)
    }

    private class FakeAuthRepository : AuthRepository {
        var savedTokens: AuthTokens? = null
            private set

        override suspend fun login(idToken: String): Result<AuthState> =
            Result.success(AuthState(isLoading = false))

        override suspend fun signup(
            idToken: String,
            nickname: String,
            profileImage: String
        ): Result<AuthState> = Result.success(AuthState(isLoading = false))

        override suspend fun reissue(refreshToken: String): Result<AuthState> =
            Result.success(AuthState(isLoading = false))

        override suspend fun updateProfile(nickname: String, profileImage: String) = Unit

        override suspend fun withdraw() = Unit

        override suspend fun logout() = Unit

        override suspend fun checkAuthStatus(): AuthState = AuthState(isLoading = false)

        override fun getStoredTokens(): AuthTokens? = savedTokens

        override fun saveTokens(tokens: AuthTokens) {
            savedTokens = tokens
        }
    }
}
