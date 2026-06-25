package com.ssafy.jjongle.oxgame.domain.navigation

import com.ssafy.jjongle.common.domain.navigation.NavRoute
import com.ssafy.jjongle.common.domain.navigation.Page

/**
 * OXGame 화면을 타입 안전하게 표현하는 라우팅 값입니다.
 *
 * 문자열 경로와 인자를 객체 안에 모아 Navigation host와 기능 모듈이 같은 이동 계약을 공유하게 합니다.
 */
object OXGamePage : Page {
    const val PATH = "/ox_game"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * OXGame Title 화면을 타입 안전하게 표현하는 라우팅 값입니다.
 *
 * 문자열 경로와 인자를 객체 안에 모아 Navigation host와 기능 모듈이 같은 이동 계약을 공유하게 합니다.
 */
object OXGameTitlePage : Page {
    const val PATH = "/ox_game_title"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * OXGame Intro 화면을 타입 안전하게 표현하는 라우팅 값입니다.
 *
 * 문자열 경로와 인자를 객체 안에 모아 Navigation host와 기능 모듈이 같은 이동 계약을 공유하게 합니다.
 */
object OXGameIntroPage : Page {
    const val PATH = "/ox_game_intro"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * OXGame Tutorial 화면을 타입 안전하게 표현하는 라우팅 값입니다.
 *
 * 문자열 경로와 인자를 객체 안에 모아 Navigation host와 기능 모듈이 같은 이동 계약을 공유하게 합니다.
 */
object OXGameTutorialPage : Page {
    const val PATH = "/ox_game_tutorial"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}
