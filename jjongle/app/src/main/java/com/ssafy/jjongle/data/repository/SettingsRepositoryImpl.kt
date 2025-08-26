package com.ssafy.jjongle.data.repository

import android.content.SharedPreferences
import com.ssafy.jjongle.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {
    
    private val _bgmEnabled = MutableStateFlow(
        sharedPreferences.getBoolean(BGM_ENABLED_KEY, true)
    )
    
    override fun getBgmEnabled(): Flow<Boolean> = _bgmEnabled.asStateFlow()
    
    override suspend fun setBgmEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(BGM_ENABLED_KEY, enabled)
            .apply()
        _bgmEnabled.value = enabled
    }
    
    companion object {
        private const val BGM_ENABLED_KEY = "bgm_enabled"
    }
}