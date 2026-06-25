package com.ssafy.jjongle.common.domain.usecase

import com.ssafy.jjongle.common.domain.base.BaseUseCase
import com.ssafy.jjongle.common.domain.helper.MessageHelper
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.helper.ResourceHelper
import com.ssafy.jjongle.tti.TTIHelper
import com.ssafy.jjongle.common.domain.repository.AuthRepository
import com.ssafy.jjongle.common.entity.AuthState
import javax.inject.Inject

/**
 * Auth 시나리오를 실행하는 domain 계층 유스케이스입니다.
 *
 * ViewModel이 repository 세부 구현을 알지 않고 하나의 사용자 흐름이나 비즈니스 작업만 호출하도록 합니다.
 */
class AuthUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    resourceHelper: ResourceHelper,
    messageHelper: MessageHelper,
    navigationHelper: NavigationHelper,
    ttiHelper: TTIHelper,
) : BaseUseCase(resourceHelper, messageHelper, navigationHelper, ttiHelper) {

    suspend fun login(idToken: String): Result<AuthState> =
        executeWithCommonHttpHandling { authRepository.login(idToken) }

    suspend fun signup(
        idToken: String,
        nickname: String,
        profileImage: String,
    ): Result<AuthState> =
        executeWithCommonHttpHandling { authRepository.signup(idToken, nickname, profileImage) }

    suspend fun updateProfile(nickname: String, profileImage: String): Result<Unit> =
        executeWithCommonHttpHandling { authRepository.updateProfile(nickname, profileImage) }

    suspend fun withdraw(): Result<Unit> =
        executeWithCommonHttpHandling { authRepository.withdraw() }

    suspend fun logout(): Result<Unit> =
        executeWithCommonHttpHandling { authRepository.logout() }

    suspend fun checkAuthStatus(): Result<AuthState> =
        executeWithCommonHttpHandling { authRepository.checkAuthStatus() }
}
