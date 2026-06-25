package com.ssafy.jjongle.common.domain.navigation

/**
 * Nav Route는 공통 흐름에서 계층 사이로 전달되는 도메인 값입니다.
 *
 * 원시 값 여러 개를 그대로 넘기지 않고 이름 있는 타입으로 묶어 호출 의도를 명확히 합니다.
 */
data class NavRoute(
    val path: String,
    val args: Map<String, String> = emptyMap(),
)

/**
 * Page 화면을 타입 안전하게 표현하는 라우팅 값입니다.
 *
 * 문자열 경로와 인자를 객체 안에 모아 Navigation host와 기능 모듈이 같은 이동 계약을 공유하게 합니다.
 */
interface Page {
    /**
     * 현재 page를 navigation host가 이해할 수 있는 [NavRoute]로 변환합니다.
     */
    fun toRoute(): NavRoute
}
