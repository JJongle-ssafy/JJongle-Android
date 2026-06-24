package com.ssafy.jjongle.oxgame.domain.navigation

import com.ssafy.jjongle.common.domain.navigation.NavRoute
import com.ssafy.jjongle.common.domain.navigation.Page

object OXGamePage : Page {
    const val PATH = "/ox_game"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

object OXGameTitlePage : Page {
    const val PATH = "/ox_game_title"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

object OXGameIntroPage : Page {
    const val PATH = "/ox_game_intro"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

object OXGameTutorialPage : Page {
    const val PATH = "/ox_game_tutorial"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}
