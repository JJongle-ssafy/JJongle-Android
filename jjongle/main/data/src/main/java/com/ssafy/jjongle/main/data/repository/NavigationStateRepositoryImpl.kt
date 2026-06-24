package com.ssafy.jjongle.main.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.ssafy.jjongle.main.domain.repository.NavigationStateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

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
