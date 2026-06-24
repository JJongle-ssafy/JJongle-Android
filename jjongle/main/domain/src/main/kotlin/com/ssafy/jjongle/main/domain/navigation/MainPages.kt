package com.ssafy.jjongle.main.domain.navigation

import com.ssafy.jjongle.common.domain.navigation.NavRoute
import com.ssafy.jjongle.common.domain.navigation.Page

object SplashPage : Page {
    const val PATH = "/splash"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

object LoginPage : Page {
    const val PATH = "/login"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

data class SignupPage(
    val idToken: String,
) : Page {
    override fun toRoute(): NavRoute = NavRoute(PATH, mapOf(ARG_ID_TOKEN to idToken))

    companion object {
        const val PATH = "/signup"
        const val ARG_ID_TOKEN = "idToken"
    }
}

object MapPage : Page {
    const val PATH = "/map"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

object MyPagePage : Page {
    const val PATH = "/my_page"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

object QuizNotePage : Page {
    const val PATH = "/quiz_note"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

object AnimalBookPage : Page {
    const val PATH = "/animal_book"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

object SettingPage : Page {
    const val PATH = "/setting"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

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
