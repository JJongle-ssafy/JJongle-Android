package com.ssafy.jjongle.domain.usecase

import com.ssafy.jjongle.domain.repository.TangramGameRepository
import javax.inject.Inject

class GetTangramHistoriesUseCase @Inject constructor(
    private val repo: TangramGameRepository
) {
    suspend operator fun invoke(page: Int = 0, size: Int = 200) =
        repo.getTangramHistories(page, size)
}