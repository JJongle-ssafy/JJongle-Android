package com.ssafy.jjongle.main.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.ssafy.jjongle.main.domain.repository.NavigationStateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * NavigationStateRepositoryImpl 저장소 계약의 data 계층 구현입니다.
 *
 * - 계층: main/data
 * - 책임: 데이터 원본을 조합하고 domain 계층이 기대하는 모델로 반환합니다.
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
