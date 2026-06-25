package com.ssafy.jjongle.main.domain.navigation

import com.ssafy.jjongle.common.domain.navigation.NavRoute
import com.ssafy.jjongle.common.domain.navigation.Page

/**
 * Splash 화면을 타입 안전하게 표현하는 라우팅 값입니다.
 *
 * 문자열 경로와 인자를 객체 안에 모아 Navigation host와 기능 모듈이 같은 이동 계약을 공유하게 합니다.
 */
object SplashPage : Page {
    const val PATH = "/splash"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * Login 화면을 타입 안전하게 표현하는 라우팅 값입니다.
 *
 * 문자열 경로와 인자를 객체 안에 모아 Navigation host와 기능 모듈이 같은 이동 계약을 공유하게 합니다.
 */
object LoginPage : Page {
    const val PATH = "/login"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * Signup 화면을 타입 안전하게 표현하는 라우팅 값입니다.
 *
 * 문자열 경로와 인자를 객체 안에 모아 Navigation host와 기능 모듈이 같은 이동 계약을 공유하게 합니다.
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
 * Map 화면을 타입 안전하게 표현하는 라우팅 값입니다.
 *
 * 문자열 경로와 인자를 객체 안에 모아 Navigation host와 기능 모듈이 같은 이동 계약을 공유하게 합니다.
 */
object MapPage : Page {
    const val PATH = "/map"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * My Page 화면을 타입 안전하게 표현하는 라우팅 값입니다.
 *
 * 문자열 경로와 인자를 객체 안에 모아 Navigation host와 기능 모듈이 같은 이동 계약을 공유하게 합니다.
 */
object MyPagePage : Page {
    const val PATH = "/my_page"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * Quiz Note 화면을 타입 안전하게 표현하는 라우팅 값입니다.
 *
 * 문자열 경로와 인자를 객체 안에 모아 Navigation host와 기능 모듈이 같은 이동 계약을 공유하게 합니다.
 */
object QuizNotePage : Page {
    const val PATH = "/quiz_note"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * Animal Book 화면을 타입 안전하게 표현하는 라우팅 값입니다.
 *
 * 문자열 경로와 인자를 객체 안에 모아 Navigation host와 기능 모듈이 같은 이동 계약을 공유하게 합니다.
 */
object AnimalBookPage : Page {
    const val PATH = "/animal_book"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * Setting 화면을 타입 안전하게 표현하는 라우팅 값입니다.
 *
 * 문자열 경로와 인자를 객체 안에 모아 Navigation host와 기능 모듈이 같은 이동 계약을 공유하게 합니다.
 */
object SettingPage : Page {
    const val PATH = "/setting"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

/**
 * Camera 화면을 타입 안전하게 표현하는 라우팅 값입니다.
 *
 * 문자열 경로와 인자를 객체 안에 모아 Navigation host와 기능 모듈이 같은 이동 계약을 공유하게 합니다.
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
