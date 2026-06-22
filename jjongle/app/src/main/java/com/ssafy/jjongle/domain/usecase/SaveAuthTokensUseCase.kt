package com.ssafy.jjongle.domain.usecase

import com.ssafy.jjongle.domain.entity.AuthError
import com.ssafy.jjongle.domain.entity.AuthException
import com.ssafy.jjongle.domain.entity.AuthTokens
import com.ssafy.jjongle.domain.repository.AuthRepository
import javax.inject.Inject

class SaveAuthTokensUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(accessToken: String, refreshToken: String): Result<Unit> {
        val tokens = AuthTokens(accessToken = accessToken, refreshToken = refreshToken)
        if (!tokens.isValid) {
            return Result.failure(
                AuthException(AuthError.MissingToken, "저장할 인증 토큰이 비어 있습니다.")
            )
        }

        authRepository.saveTokens(tokens)
        return Result.success(Unit)
    }
}
