package com.ssafy.jjongle.common.data.repository

import android.content.SharedPreferences
import com.ssafy.jjongle.common.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Settings 저장소 계약을 data 계층에서 구현합니다.
 *
 * 원격/로컬 DataSource를 조합하고 DTO나 DB 모델을 앱 내부 모델로 변환해 domain 계층에 반환합니다.
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : SettingsRepository {

    private val bgmEnabled = MutableStateFlow(
        sharedPreferences.getBoolean(BGM_ENABLED_KEY, true)
    )

    override fun getBgmEnabled(): Flow<Boolean> = bgmEnabled.asStateFlow()

    override suspend fun setBgmEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(BGM_ENABLED_KEY, enabled)
            .apply()
        bgmEnabled.value = enabled
    }

    private companion object {
        const val BGM_ENABLED_KEY = "bgm_enabled"
    }
}
