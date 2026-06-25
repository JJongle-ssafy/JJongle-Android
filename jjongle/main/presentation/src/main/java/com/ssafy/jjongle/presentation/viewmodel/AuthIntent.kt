package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.presentation.mvi.MviIntent

/**
 * AuthIntent 화면에서 ViewModel로 전달되는 사용자 입력을 정의합니다.
 *
 * - 계층: main/presentation
 * - 책임: UI 이벤트를 MVI intent로 분리해 상태 변경 진입점을 명확히 합니다.
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
