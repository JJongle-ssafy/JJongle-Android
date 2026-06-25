package com.ssafy.jjongle.common.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * SettingsRepository domain 계층이 의존하는 저장소 계약입니다.
 *
 * - 계층: common/domain
 * - 책임: data 구현을 숨기고 유스케이스에 필요한 작업만 노출합니다.
 */
interface SettingsRepository {
    fun getBgmEnabled(): Flow<Boolean>
    suspend fun setBgmEnabled(enabled: Boolean)
}
