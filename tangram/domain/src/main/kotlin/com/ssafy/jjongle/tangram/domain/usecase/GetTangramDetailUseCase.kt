package com.ssafy.jjongle.tangram.domain.usecase

import com.ssafy.jjongle.common.domain.base.BaseUseCase
import com.ssafy.jjongle.common.domain.helper.MessageHelper
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.helper.ResourceHelper
import com.ssafy.jjongle.tti.TTIHelper
import com.ssafy.jjongle.tangram.domain.repository.TangramGameRepository
import com.ssafy.jjongle.common.entity.AnimalType
import com.ssafy.jjongle.tangram.entity.TangramDetail
import javax.inject.Inject

/**
 * GetTangramDetailUseCase 비즈니스 시나리오를 실행하는 유스케이스입니다.
 *
 * - 계층: tangram/domain
 * - 책임: ViewModel이 필요한 domain 작업을 단일 진입점으로 제공합니다.
 */
class GetTangramDetailUseCase @Inject constructor(
    private val repo: TangramGameRepository,
    resourceHelper: ResourceHelper,
    messageHelper: MessageHelper,
    navigationHelper: NavigationHelper,
    ttiHelper: TTIHelper,
) : BaseUseCase(resourceHelper, messageHelper, navigationHelper, ttiHelper) {
    suspend operator fun invoke(id: Long, type: AnimalType): Result<TangramDetail> =
        executeWithCommonHttpHandling { repo.getTangramDetail(id, type) }
}
