package com.ssafy.jjongle.common.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Settings 기능이 domain 계층에서 기대하는 저장소 계약입니다.
 *
 * UseCase는 이 계약에만 의존하고, Firebase, Room, Retrofit 같은 실제 데이터 구현은 data 계층에 숨깁니다.
 */
interface SettingsRepository {
    fun getBgmEnabled(): Flow<Boolean>
    suspend fun setBgmEnabled(enabled: Boolean)
}
