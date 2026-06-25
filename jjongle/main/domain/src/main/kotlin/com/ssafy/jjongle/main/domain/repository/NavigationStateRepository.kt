package com.ssafy.jjongle.main.domain.repository

/**
 * NavigationStateRepository domain 계층이 의존하는 저장소 계약입니다.
 *
 * - 계층: main/domain
 * - 책임: data 구현을 숨기고 유스케이스에 필요한 작업만 노출합니다.
 */
interface NavigationStateRepository {
    suspend fun saveCurrentRoute(route: String)

    suspend fun getCurrentRoute(): String?
}
