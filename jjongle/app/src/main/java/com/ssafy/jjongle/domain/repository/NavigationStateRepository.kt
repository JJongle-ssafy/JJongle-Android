package com.ssafy.jjongle.domain.repository

/**
 * NavigationStateRepository.kt
 * This interface defines the contract for managing navigation-related state,
 * primarily for saving and retrieving the last known navigation route.
 */

interface NavigationStateRepository {

    /**
     * Saves the current navigation route.
     * 현재 내비게이션 경로를 저장합니다.
     * @param route The route string to save.
     */
    suspend fun saveCurrentRoute(route: String)

    /**
     * Retrieves the last saved navigation route.
     * 마지막으로 저장된 내비게이션 경로를 불러옵니다.
     * @return The last saved route string, or null if no route was saved.
     */
    suspend fun getCurrentRoute(): String?

    /**
     * Clears any saved navigation state.
     * 저장된 내비게이션 상태를 모두 삭제합니다.
     */
    suspend fun clearNavigationState()
}