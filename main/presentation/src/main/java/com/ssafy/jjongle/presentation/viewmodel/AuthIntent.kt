package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.presentation.mvi.MviIntent

/**
 * 메인 기능 화면에서 ViewModel로 전달되는 사용자 입력과 화면 이벤트입니다.
 *
 * 버튼 클릭, 화면 진입, 선택 변경 같은 입력을 타입으로 분리해 상태 변경의 시작점을 명확히 남깁니다.
 */
sealed interface AuthIntent : MviIntent {
    data object CheckAuthStatus : AuthIntent
    data object Logout : AuthIntent
    class ShowError(val message: String) : AuthIntent
    class LoginWithGoogleIdToken(
        val googleIdToken: String,
        val onSuccess: () -> Unit,
        val onNeedSignUp: (String) -> Unit,
        val onFailure: (Throwable) -> Unit,
    ) : AuthIntent

    class SignUp(
        val idToken: String,
        val nickname: String,
        val profileImage: String,
        val onSuccess: () -> Unit,
        val onFailure: (Throwable) -> Unit,
        val onNeedLogin: () -> Unit,
    ) : AuthIntent

    class UpdateProfile(
        val nickname: String,
        val profileImage: String,
        val onSuccess: () -> Unit,
        val onFailure: (Throwable) -> Unit,
    ) : AuthIntent

    class Withdraw(
        val onSuccess: () -> Unit,
        val onFailure: (Throwable) -> Unit,
    ) : AuthIntent
}
