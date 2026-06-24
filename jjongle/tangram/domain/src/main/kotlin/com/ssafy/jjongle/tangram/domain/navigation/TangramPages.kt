package com.ssafy.jjongle.tangram.domain.navigation

import com.ssafy.jjongle.common.domain.navigation.NavRoute
import com.ssafy.jjongle.common.domain.navigation.Page

object TangramTitlePage : Page {
    const val PATH = "/tangram_title"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

object TangramIntroPage : Page {
    const val PATH = "/tangram_intro"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

object TangramStagePage : Page {
    const val PATH = "/tangram_stage"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

object BeforeTangramTutorialPage : Page {
    const val PATH = "/before_tangram_tutorial"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}

object TangramTutorialPage : Page {
    const val PATH = "/tangram_tutorial"
    override fun toRoute(): NavRoute = NavRoute(PATH)
}
