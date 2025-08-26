package com.ssafy.jjongle.presentation.navigation

import com.ssafy.jjongle.presentation.media.BgmGroup

fun routeToBgmGroup(route: String?): BgmGroup? = when {
    route == null -> null

    // 1) Splash / Login / Map / (회원가입도 온보딩 맥락이므로 WORLD에 포함)
    route.startsWith(Screen.Splash.route) ||
            route.startsWith(Screen.Login.route) ||
            route.startsWith(Screen.Signup.route) ||
            route.startsWith(Screen.Map.route) -> BgmGroup.WORLD

    // 2) Tangram (타이틀/인트로/스테이지)
    route.startsWith(Screen.TangramTitle.route) ||
            route.startsWith(Screen.TangramIntro.route) ||
            route.startsWith(Screen.TangramStage.route) -> BgmGroup.TANGRAM

    // 3) OX (타이틀/인트로/게임)
    route.startsWith(Screen.OXGameTitle.route) ||
            route.startsWith(Screen.OXGameIntro.route) ||
            route.startsWith(Screen.OXGame.route) -> BgmGroup.OX

    // 4) 마이페이지 계열(마이페이지/지식노트/동물도감)
    route.startsWith(Screen.MyPage.route) ||
            route.startsWith(Screen.QuizNote.route) ||
            route.startsWith(Screen.AnimalBook.route) -> BgmGroup.MYPAGE

    else -> null
}
