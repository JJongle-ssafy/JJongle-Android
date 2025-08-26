package com.ssafy.jjongle.domain.usecase

import com.ssafy.jjongle.domain.entity.AnimalType
import com.ssafy.jjongle.domain.repository.TangramGameRepository
import javax.inject.Inject

class GetTangramDetailUseCase @Inject constructor(
    private val repo: TangramGameRepository
) {
    suspend operator fun invoke(id: Long, type: AnimalType) =
        repo.getTangramDetail(id, type)
}