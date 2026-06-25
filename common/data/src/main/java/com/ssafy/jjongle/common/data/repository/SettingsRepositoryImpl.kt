package com.ssafy.jjongle.common.data.repository

import android.content.SharedPreferences
import com.ssafy.jjongle.common.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SettingsRepositoryImpl 저장소 계약의 data 계층 구현입니다.
 *
 * - 계층: common/data
 * - 책임: 데이터 원본을 조합하고 domain 계층이 기대하는 모델로 반환합니다.
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
