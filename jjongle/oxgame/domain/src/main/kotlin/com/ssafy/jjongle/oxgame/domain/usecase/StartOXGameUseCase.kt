package com.ssafy.jjongle.oxgame.domain.usecase

import com.ssafy.jjongle.common.domain.base.BaseUseCase
import com.ssafy.jjongle.common.domain.helper.MessageHelper
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.helper.ResourceHelper
import com.ssafy.jjongle.tti.TTIHelper
import com.ssafy.jjongle.oxgame.domain.repository.OXGameRepository
import javax.inject.Inject

class StartOXGameUseCase @Inject constructor(
    private val oxGameRepository: OXGameRepository,
    resourceHelper: ResourceHelper,
    messageHelper: MessageHelper,
    navigationHelper: NavigationHelper,
    ttiHelper: TTIHelper,
) : BaseUseCase(resourceHelper, messageHelper, navigationHelper, ttiHelper) {
    /**
     * 로컬 OX 게임 세션을 시작합니다.
     */
    suspend fun startGameSession(): Result<Unit> =
        executeWithCommonHttpHandling { oxGameRepository.startGameSession() }

    /**
     * 현재 저장된 세션 키를 반환합니다.
     */
    fun getSessionKey(): String? {
        return oxGameRepository.getSessionKey()
    }

    /**
     * 세션 키를 저장합니다.
     */
    fun saveSessionKey(sessionKey: String) {
        oxGameRepository.saveSessionKey(sessionKey)
    }
}
