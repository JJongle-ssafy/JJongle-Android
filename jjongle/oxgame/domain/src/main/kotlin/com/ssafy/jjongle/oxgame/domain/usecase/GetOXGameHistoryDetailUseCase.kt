package com.ssafy.jjongle.oxgame.domain.usecase

import com.ssafy.jjongle.common.domain.base.BaseUseCase
import com.ssafy.jjongle.common.domain.helper.MessageHelper
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.helper.ResourceHelper
import com.ssafy.jjongle.tti.TTIHelper
import com.ssafy.jjongle.oxgame.domain.repository.OXGameHistoryRepository
import com.ssafy.jjongle.oxgame.entity.OXGameWrongAnswerNote
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList

class GetOXGameHistoryDetailUseCase @Inject constructor(
    private val repo: OXGameHistoryRepository,
    resourceHelper: ResourceHelper,
    messageHelper: MessageHelper,
    navigationHelper: NavigationHelper,
    ttiHelper: TTIHelper,
) : BaseUseCase(resourceHelper, messageHelper, navigationHelper, ttiHelper) {

    suspend operator fun invoke(historyId: Long): Result<ImmutableList<OXGameWrongAnswerNote>> =
        executeWithCommonHttpHandling { repo.getHistoryDetail(historyId) }
}
