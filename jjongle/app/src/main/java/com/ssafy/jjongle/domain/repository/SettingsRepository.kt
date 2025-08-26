package com.ssafy.jjongle.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getBgmEnabled(): Flow<Boolean>
    suspend fun setBgmEnabled(enabled: Boolean)
}