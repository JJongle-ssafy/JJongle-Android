package com.ssafy.jjongle.tangram.domain.usecase

import com.ssafy.jjongle.common.domain.base.BaseUseCase
import com.ssafy.jjongle.common.domain.helper.MessageHelper
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.helper.ResourceHelper
import com.ssafy.jjongle.tti.TTIHelper
import com.ssafy.jjongle.tangram.domain.repository.TangramGameRepository
import com.ssafy.jjongle.tangram.entity.TangramHistoriesPage
import javax.inject.Inject

/**
 * Get Tangram Histories 시나리오를 실행하는 domain 계층 유스케이스입니다.
 *
 * ViewModel이 repository 세부 구현을 알지 않고 하나의 사용자 흐름이나 비즈니스 작업만 호출하도록 합니다.
 */
class GetTangramHistoriesUseCase @Inject constructor(
    private val repo: TangramGameRepository,
    resourceHelper: ResourceHelper,
    messageHelper: MessageHelper,
    navigationHelper: NavigationHelper,
    ttiHelper: TTIHelper,
) : BaseUseCase(resourceHelper, messageHelper, navigationHelper, ttiHelper) {
    suspend operator fun invoke(page: Int = 0, size: Int = 200): Result<TangramHistoriesPage> =
        executeWithCommonHttpHandling { repo.getTangramHistories(page, size) }
}
