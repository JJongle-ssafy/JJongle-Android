package com.ssafy.jjongle.main.domain.navigation

import com.ssafy.jjongle.common.domain.navigation.NavRoute
import com.ssafy.jjongle.common.domain.navigation.Page

/**
 * SplashPage Navigation3에서 사용하는 페이지 계약입니다.
 *
 * - 계층: main/domain
 * - 책임: 기능 모듈의 화면 목적지를 타입 안전한 라우팅 값으로 표현합니다.
 */
object SplashPage : Page {
    const val PATH = "/splash"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * LoginPage Navigation3에서 사용하는 페이지 계약입니다.
 *
 * - 계층: main/domain
 * - 책임: 기능 모듈의 화면 목적지를 타입 안전한 라우팅 값으로 표현합니다.
 */
object LoginPage : Page {
    const val PATH = "/login"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * SignupPage Navigation3에서 사용하는 페이지 계약입니다.
 *
 * - 계층: main/domain
 * - 책임: 기능 모듈의 화면 목적지를 타입 안전한 라우팅 값으로 표현합니다.
 */
data class SignupPage(
    val idToken: String,
) : Page {
    override fun toRoute(): NavRoute = NavRoute(PATH, mapOf(ARG_ID_TOKEN to idToken))

    companion object {
        const val PATH = "/signup"
        const val ARG_ID_TOKEN = "idToken"
    }
}

/**
 * MapPage Navigation3에서 사용하는 페이지 계약입니다.
 *
 * - 계층: main/domain
 * - 책임: 기능 모듈의 화면 목적지를 타입 안전한 라우팅 값으로 표현합니다.
 */
object MapPage : Page {
    const val PATH = "/map"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * MyPagePage Navigation3에서 사용하는 페이지 계약입니다.
 *
 * - 계층: main/domain
 * - 책임: 기능 모듈의 화면 목적지를 타입 안전한 라우팅 값으로 표현합니다.
 */
object MyPagePage : Page {
    const val PATH = "/my_page"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * QuizNotePage Navigation3에서 사용하는 페이지 계약입니다.
 *
 * - 계층: main/domain
 * - 책임: 기능 모듈의 화면 목적지를 타입 안전한 라우팅 값으로 표현합니다.
 */
object QuizNotePage : Page {
    const val PATH = "/quiz_note"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * AnimalBookPage Navigation3에서 사용하는 페이지 계약입니다.
 *
 * - 계층: main/domain
 * - 책임: 기능 모듈의 화면 목적지를 타입 안전한 라우팅 값으로 표현합니다.
 */
object AnimalBookPage : Page {
    const val PATH = "/animal_book"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * SettingPage Navigation3에서 사용하는 페이지 계약입니다.
 *
 * - 계층: main/domain
 * - 책임: 기능 모듈의 화면 목적지를 타입 안전한 라우팅 값으로 표현합니다.
 */
object SettingPage : Page {
    const val PATH = "/setting"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * CameraPage Navigation3에서 사용하는 페이지 계약입니다.
 *
 * - 계층: main/domain
 * - 책임: 기능 모듈의 화면 목적지를 타입 안전한 라우팅 값으로 표현합니다.
 */
data class CameraPage(
    val animal: String,
) : Page {
    override fun toRoute(): NavRoute = NavRoute(PATH, mapOf(ARG_ANIMAL to animal))

    fun routePath(): String = "/camera/$animal"

    companion object {
        const val PATH = "/camera/{animal}"
        const val ARG_ANIMAL = "animal"
        fun routePath(animal: String): String = "/camera/$animal"
    }
}
