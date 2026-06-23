package com.ssafy.jjongle.common.domain.usecase

import com.ssafy.jjongle.common.domain.repository.TangramGameRepository
import javax.inject.Inject

class TangramGameUseCase @Inject constructor(
    private val tangramGameRepository: TangramGameRepository
) {
    
    suspend fun getCurrentChallengeStageId(): Int {
        return tangramGameRepository.getCurrentChallengeStageId()
    }
}