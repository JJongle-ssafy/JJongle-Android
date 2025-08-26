package com.ssafy.jjongle.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Signup : Screen("signup")
    object Login : Screen("login")
    object Map : Screen("map")
    object MyPage : Screen("my_page")
    object QuizNote : Screen("quiz_note")
    object AnimalBook : Screen("animal_book")
    object Setting : Screen("setting")
    object UnityGame : Screen("unity_game")
    object OXGame : Screen("ox_game")
    object OXGameTitle : Screen("ox_game_title")
    object OXGameIntro : Screen("ox_game_intro")
    object OXGameTutorial : Screen("ox_game_tutorial")
    object TangramTitle : Screen("tangram_title")
    object TangramIntro : Screen("tangram_intro")
    object TangramStage : Screen("tangram_stage")
    object BeforeTangramTutorial : Screen("before_tangram_tutorial")
    object TangramTutorial : Screen("tangram_tutorial")
    object Camera : Screen("camera/{animal}") {
        fun createRoute(animal: String): String = "camera/$animal"
    }
} 