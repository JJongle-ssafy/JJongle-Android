package com.ssafy.jjongle.main.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.ssafy.jjongle.main.domain.repository.NavigationStateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Navigation State 저장소 계약을 data 계층에서 구현합니다.
 *
 * 원격/로컬 DataSource를 조합하고 DTO나 DB 모델을 앱 내부 모델로 변환해 domain 계층에 반환합니다.
 */
@Singleton
class NavigationStateRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : NavigationStateRepository {

    override suspend fun saveCurrentRoute(route: String) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit {
                putString(KEY_CURRENT_ROUTE, route)
            }
        }
    }

    override suspend fun getCurrentRoute(): String? =
        withContext(Dispatchers.IO) {
            sharedPreferences.getString(KEY_CURRENT_ROUTE, null)
        }

    private companion object {
        const val KEY_CURRENT_ROUTE = "current_navigation_route"
    }
}
