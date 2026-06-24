package com.ssafy.jjongle.oxgame.domain.usecase

import com.ssafy.jjongle.common.domain.base.BaseUseCase
import com.ssafy.jjongle.common.domain.helper.MessageHelper
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.helper.ResourceHelper
import com.ssafy.jjongle.tti.TTIHelper
import com.ssafy.jjongle.oxgame.domain.repository.OXGameHistoryPage
import com.ssafy.jjongle.oxgame.domain.repository.OXGameHistoryRepository
import javax.inject.Inject

class GetOXGameHistoriesUseCase @Inject constructor(
    private val repo: OXGameHistoryRepository,
    resourceHelper: ResourceHelper,
    messageHelper: MessageHelper,
    navigationHelper: NavigationHelper,
    ttiHelper: TTIHelper,
) : BaseUseCase(resourceHelper, messageHelper, navigationHelper, ttiHelper) {

    suspend operator fun invoke(page: Int): Result<OXGameHistoryPage> =
        executeWithCommonHttpHandling { repo.getHistories(page) }
}
