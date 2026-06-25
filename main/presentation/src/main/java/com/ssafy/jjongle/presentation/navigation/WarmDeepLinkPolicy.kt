package com.ssafy.jjongle.presentation.navigation

/**
 * List Navigation3 라우팅 계약을 표현합니다.
 *
 * - 계층: main/presentation
 * - 책임: 앱 셸과 기능 모듈 사이의 화면 이동 경계를 정의합니다.
 */
fun List<GenericNavKey>.bringToFront(target: GenericNavKey): List<GenericNavKey> =
    filterNot { it == target } + target
