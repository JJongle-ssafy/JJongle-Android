package com.ssafy.jjongle.oxgame.domain.usecase

import com.ssafy.jjongle.common.domain.base.BaseUseCase
import com.ssafy.jjongle.common.domain.helper.MessageHelper
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.helper.ResourceHelper
import com.ssafy.jjongle.tti.TTIHelper
import com.ssafy.jjongle.oxgame.domain.repository.OXGameHistoryPage
import com.ssafy.jjongle.oxgame.domain.repository.OXGameHistoryRepository
import javax.inject.Inject

/**
 * Get OXGame Histories 시나리오를 실행하는 domain 계층 유스케이스입니다.
 *
 * ViewModel이 repository 세부 구현을 알지 않고 하나의 사용자 흐름이나 비즈니스 작업만 호출하도록 합니다.
 */
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
