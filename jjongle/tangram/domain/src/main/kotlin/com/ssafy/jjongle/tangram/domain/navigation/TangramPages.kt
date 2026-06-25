package com.ssafy.jjongle.tangram.domain.navigation

import com.ssafy.jjongle.common.domain.navigation.NavRoute
import com.ssafy.jjongle.common.domain.navigation.Page

/**
 * TangramTitlePage Navigation3에서 사용하는 페이지 계약입니다.
 *
 * - 계층: tangram/domain
 * - 책임: 기능 모듈의 화면 목적지를 타입 안전한 라우팅 값으로 표현합니다.
 */
object TangramTitlePage : Page {
    const val PATH = "/tangram_title"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * TangramIntroPage Navigation3에서 사용하는 페이지 계약입니다.
 *
 * - 계층: tangram/domain
 * - 책임: 기능 모듈의 화면 목적지를 타입 안전한 라우팅 값으로 표현합니다.
 */
object TangramIntroPage : Page {
    const val PATH = "/tangram_intro"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * TangramStagePage Navigation3에서 사용하는 페이지 계약입니다.
 *
 * - 계층: tangram/domain
 * - 책임: 기능 모듈의 화면 목적지를 타입 안전한 라우팅 값으로 표현합니다.
 */
object TangramStagePage : Page {
    const val PATH = "/tangram_stage"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * BeforeTangramTutorialPage Navigation3에서 사용하는 페이지 계약입니다.
 *
 * - 계층: tangram/domain
 * - 책임: 기능 모듈의 화면 목적지를 타입 안전한 라우팅 값으로 표현합니다.
 */
object BeforeTangramTutorialPage : Page {
    const val PATH = "/before_tangram_tutorial"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * TangramTutorialPage Navigation3에서 사용하는 페이지 계약입니다.
 *
 * - 계층: tangram/domain
 * - 책임: 기능 모듈의 화면 목적지를 타입 안전한 라우팅 값으로 표현합니다.
 */
object TangramTutorialPage : Page {
    const val PATH = "/tangram_tutorial"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}
