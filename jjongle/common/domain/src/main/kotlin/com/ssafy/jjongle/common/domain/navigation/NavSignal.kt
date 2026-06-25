package com.ssafy.jjongle.common.domain.navigation

/**
 * NavSignal Navigation3 라우팅 계약을 표현합니다.
 *
 * - 계층: common/domain
 * - 책임: 앱 셸과 기능 모듈 사이의 화면 이동 경계를 정의합니다.
 */
sealed interface NavSignal {
    /**
     * 앱 내부에서 목적지 화면으로 이동하는 일반 신호입니다.
     */
    data class GoToDestPage(val route: NavRoute) : NavSignal

    /**
     * 이미 실행 중인 앱이 딥링크를 받았을 때 사용하는 신호입니다.
     *
     * navigation host는 기존 스택을 보존하면서 대상 화면을 앞으로 가져오는 정책을 적용할 수 있습니다.
     */
    data class DeepLink(val route: NavRoute) : NavSignal

    /**
     * 현재 navigation stack에서 한 단계 뒤로 이동하라는 신호입니다.
     */
    data object Back : NavSignal
}
