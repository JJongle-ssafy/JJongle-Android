package com.ssafy.jjongle.common.domain.usecase

import com.ssafy.jjongle.common.entity.AuthTokens
import com.ssafy.jjongle.common.domain.repository.AuthRepository
import javax.inject.Inject

@Deprecated("Legacy backend token use case retained for legacy backend compatibility.")
class GetAuthTokensUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): AuthTokens? = authRepository.getStoredTokens()
}
