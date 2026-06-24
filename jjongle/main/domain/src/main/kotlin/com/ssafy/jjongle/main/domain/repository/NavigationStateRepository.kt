package com.ssafy.jjongle.main.domain.repository

interface NavigationStateRepository {
    suspend fun saveCurrentRoute(route: String)

    suspend fun getCurrentRoute(): String?
}
