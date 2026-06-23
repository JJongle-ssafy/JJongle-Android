package com.ssafy.jjongle.common.domain.usecase

import com.ssafy.jjongle.common.entity.AnimalType
import com.ssafy.jjongle.common.domain.repository.TangramGameRepository
import javax.inject.Inject

class GetTangramDetailUseCase @Inject constructor(
    private val repo: TangramGameRepository
) {
    suspend operator fun invoke(id: Long, type: AnimalType) =
        repo.getTangramDetail(id, type)
}