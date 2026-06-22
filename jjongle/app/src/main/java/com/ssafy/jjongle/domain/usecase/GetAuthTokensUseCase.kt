package com.ssafy.jjongle.domain.usecase

import com.ssafy.jjongle.domain.entity.AuthTokens
import com.ssafy.jjongle.domain.repository.AuthRepository
import javax.inject.Inject

class GetAuthTokensUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): AuthTokens? = authRepository.getStoredTokens()
}
