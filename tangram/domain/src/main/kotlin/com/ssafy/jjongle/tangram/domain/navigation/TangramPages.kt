package com.ssafy.jjongle.tangram.domain.navigation

import com.ssafy.jjongle.common.domain.navigation.NavRoute
import com.ssafy.jjongle.common.domain.navigation.Page

/**
 * Tangram Title 화면을 타입 안전하게 표현하는 라우팅 값입니다.
 *
 * 문자열 경로와 인자를 객체 안에 모아 Navigation host와 기능 모듈이 같은 이동 계약을 공유하게 합니다.
 */
object TangramTitlePage : Page {
    const val PATH = "/tangram_title"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * Tangram Intro 화면을 타입 안전하게 표현하는 라우팅 값입니다.
 *
 * 문자열 경로와 인자를 객체 안에 모아 Navigation host와 기능 모듈이 같은 이동 계약을 공유하게 합니다.
 */
object TangramIntroPage : Page {
    const val PATH = "/tangram_intro"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * Tangram Stage 화면을 타입 안전하게 표현하는 라우팅 값입니다.
 *
 * 문자열 경로와 인자를 객체 안에 모아 Navigation host와 기능 모듈이 같은 이동 계약을 공유하게 합니다.
 */
object TangramStagePage : Page {
    const val PATH = "/tangram_stage"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * Before Tangram Tutorial 화면을 타입 안전하게 표현하는 라우팅 값입니다.
 *
 * 문자열 경로와 인자를 객체 안에 모아 Navigation host와 기능 모듈이 같은 이동 계약을 공유하게 합니다.
 */
object BeforeTangramTutorialPage : Page {
    const val PATH = "/before_tangram_tutorial"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * Tangram Tutorial 화면을 타입 안전하게 표현하는 라우팅 값입니다.
 *
 * 문자열 경로와 인자를 객체 안에 모아 Navigation host와 기능 모듈이 같은 이동 계약을 공유하게 합니다.
 */
object TangramTutorialPage : Page {
    const val PATH = "/tangram_tutorial"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}
