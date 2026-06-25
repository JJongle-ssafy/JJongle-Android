package com.ssafy.jjongle.oxgame.domain.navigation

import com.ssafy.jjongle.common.domain.navigation.NavRoute
import com.ssafy.jjongle.common.domain.navigation.Page

/**
 * OXGamePage Navigation3에서 사용하는 페이지 계약입니다.
 *
 * - 계층: oxgame/domain
 * - 책임: 기능 모듈의 화면 목적지를 타입 안전한 라우팅 값으로 표현합니다.
 */
object OXGamePage : Page {
    const val PATH = "/ox_game"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * OXGameTitlePage Navigation3에서 사용하는 페이지 계약입니다.
 *
 * - 계층: oxgame/domain
 * - 책임: 기능 모듈의 화면 목적지를 타입 안전한 라우팅 값으로 표현합니다.
 */
object OXGameTitlePage : Page {
    const val PATH = "/ox_game_title"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * OXGameIntroPage Navigation3에서 사용하는 페이지 계약입니다.
 *
 * - 계층: oxgame/domain
 * - 책임: 기능 모듈의 화면 목적지를 타입 안전한 라우팅 값으로 표현합니다.
 */
object OXGameIntroPage : Page {
    const val PATH = "/ox_game_intro"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * OXGameTutorialPage Navigation3에서 사용하는 페이지 계약입니다.
 *
 * - 계층: oxgame/domain
 * - 책임: 기능 모듈의 화면 목적지를 타입 안전한 라우팅 값으로 표현합니다.
 */
object OXGameTutorialPage : Page {
    const val PATH = "/ox_game_tutorial"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}
