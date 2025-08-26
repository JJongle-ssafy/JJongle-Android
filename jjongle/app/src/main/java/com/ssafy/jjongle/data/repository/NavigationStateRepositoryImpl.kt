package com.ssafy.jjongle.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.ssafy.jjongle.domain.repository.NavigationStateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * NavigationStateRepository.kt
 * This class implements the NavigationStateRepository interface using SharedPreferences
 * for data persistence.
 * SharedPreferences를 사용하여 NavigationStateRepository 인터페이스를 구현합니다.
 */
@Singleton // Hilt를 통해 싱글톤으로 관리
class NavigationStateRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences // Hilt 모듈로부터 주입
) : NavigationStateRepository {

    override suspend fun saveCurrentRoute(route: String) {
        withContext(Dispatchers.IO) { // IO 작업은 IO 스레드에서
            sharedPreferences.edit {
                putString(KEY_CURRENT_ROUTE, route)
            }
        }
    }

    override suspend fun getCurrentRoute(): String? {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getString(KEY_CURRENT_ROUTE, null)
        }
    }

    override suspend fun clearNavigationState() {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit {
                remove(KEY_CURRENT_ROUTE)
            }
        }
    }

    companion object {
        private const val KEY_CURRENT_ROUTE = "current_navigation_route"
    }
}
