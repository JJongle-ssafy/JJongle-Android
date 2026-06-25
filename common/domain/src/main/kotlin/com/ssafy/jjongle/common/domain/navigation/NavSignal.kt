package com.ssafy.jjongle.common.domain.navigation

/**
 * Nav Signal는 공통 흐름에서 허용되는 상태나 이벤트 종류를 제한합니다.
 *
 * 문자열이나 숫자 상수 대신 타입 분기로 처리해 잘못된 값이 전달되는 일을 줄입니다.
 */
sealed interface NavSignal {
    /**
     * 앱 내부에서 목적지 화면으로 이동하는 일반 신호입니다.
     */
    data class GoToDestPage(val route: NavRoute) : NavSignal

    /**
 * Deep Link는 공통 흐름에서 계층 사이로 전달되는 도메인 값입니다.
 *
 * 원시 값 여러 개를 그대로 넘기지 않고 이름 있는 타입으로 묶어 호출 의도를 명확히 합니다.
 */
    data class DeepLink(val route: NavRoute) : NavSignal

    /**
     * 현재 navigation stack에서 한 단계 뒤로 이동하라는 신호입니다.
     */
    data object Back : NavSignal
}
