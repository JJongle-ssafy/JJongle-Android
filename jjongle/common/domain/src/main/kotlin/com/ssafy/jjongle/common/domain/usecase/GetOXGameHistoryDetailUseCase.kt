package com.ssafy.jjongle.common.domain.usecase

import com.ssafy.jjongle.common.domain.repository.OXGameHistoryRepository
import javax.inject.Inject

class GetOXGameHistoryDetailUseCase @Inject constructor(
    private val repo: OXGameHistoryRepository
) {
    suspend operator fun invoke(historyId: Long) = repo.getHistoryDetail(historyId)
}