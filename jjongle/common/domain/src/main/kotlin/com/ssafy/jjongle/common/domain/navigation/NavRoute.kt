package com.ssafy.jjongle.common.domain.navigation

/**
 * NavRoute Navigation3 라우팅 계약을 표현합니다.
 *
 * - 계층: common/domain
 * - 책임: 앱 셸과 기능 모듈 사이의 화면 이동 경계를 정의합니다.
 */
data class NavRoute(
    val path: String,
    val args: Map<String, String> = emptyMap(),
)

/**
 * Page Navigation3에서 사용하는 페이지 계약입니다.
 *
 * - 계층: common/domain
 * - 책임: 기능 모듈의 화면 목적지를 타입 안전한 라우팅 값으로 표현합니다.
 */
interface Page {
    /**
     * 현재 page를 navigation host가 이해할 수 있는 [NavRoute]로 변환합니다.
     */
    fun toRoute(): NavRoute
}
