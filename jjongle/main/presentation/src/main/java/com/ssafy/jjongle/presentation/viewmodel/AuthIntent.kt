package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.presentation.mvi.MviIntent

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
