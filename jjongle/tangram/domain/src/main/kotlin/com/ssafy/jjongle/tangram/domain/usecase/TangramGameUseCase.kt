package com.ssafy.jjongle.tangram.domain.usecase

import com.ssafy.jjongle.common.domain.base.BaseUseCase
import com.ssafy.jjongle.common.domain.helper.MessageHelper
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.helper.ResourceHelper
import com.ssafy.jjongle.tti.TTIHelper
import com.ssafy.jjongle.tangram.domain.repository.TangramGameRepository
import javax.inject.Inject

class TangramGameUseCase @Inject constructor(
    private val tangramGameRepository: TangramGameRepository,
    resourceHelper: ResourceHelper,
    messageHelper: MessageHelper,
    navigationHelper: NavigationHelper,
    ttiHelper: TTIHelper,
) : BaseUseCase(resourceHelper, messageHelper, navigationHelper, ttiHelper) {
    
    suspend fun getCurrentChallengeStageId(): Result<Int> =
        executeWithCommonHttpHandling { tangramGameRepository.getCurrentChallengeStageId() }
}
