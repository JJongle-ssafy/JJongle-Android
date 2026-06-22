package com.ssafy.jjongle.domain.usecase

import com.ssafy.jjongle.domain.entity.AuthException
import com.ssafy.jjongle.domain.entity.AuthTokens
import com.ssafy.jjongle.domain.entity.MissingTokenAuthError
import com.ssafy.jjongle.domain.repository.AuthRepository
import javax.inject.Inject

@Deprecated("Legacy backend token use case retained for Unity/backend migration compatibility.")
class SaveAuthTokensUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(accessToken: String, refreshToken: String): Result<Unit> {
        val tokens = AuthTokens(accessToken = accessToken, refreshToken = refreshToken)
        if (!tokens.isValid) {
            return Result.failure(
                AuthException(MissingTokenAuthError, "저장할 인증 토큰이 비어 있습니다.")
            )
        }

        authRepository.saveTokens(tokens)
        return Result.success(Unit)
    }
}
