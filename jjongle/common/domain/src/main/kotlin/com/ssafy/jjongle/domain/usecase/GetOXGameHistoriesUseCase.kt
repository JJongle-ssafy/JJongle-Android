package com.ssafy.jjongle.domain.usecase

import com.ssafy.jjongle.domain.repository.OXGameHistoryRepository
import javax.inject.Inject

class GetOXGameHistoriesUseCase @Inject constructor(
    private val repo: OXGameHistoryRepository
) {
    suspend operator fun invoke(page: Int) = repo.getHistories(page)
}