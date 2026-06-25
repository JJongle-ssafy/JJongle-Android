package com.ssafy.jjongle.presentation.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.ssafy.jjongle.main.presentation.R
import com.ssafy.jjongle.common.presentation.ui.component.ArchiText
import com.ssafy.jjongle.common.presentation.ui.layout.SystemBackgroundImageEffect
import com.ssafy.jjongle.common.presentation.ui.theme.ArchiThemeImpl
import com.ssafy.jjongle.presentation.viewmodel.AuthIntent
import com.ssafy.jjongle.presentation.viewmodel.AuthViewModel

/**
 * LoginScreen Compose UI를 구성합니다.
 *
 * - 계층: main/presentation
 * - 책임: 상태를 표시하고 사용자 이벤트를 상위 콜백이나 ViewModel로 전달합니다.
 */
@Composable
fun LoginScreen(

    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToMap: () -> Unit,
    onNavigateToSignUp: (String) -> Unit,   // 회원가입 화면으로 이동하는 콜백

) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val authState = uiState.authState
    val serverClientId = context.getString(R.string.default_web_client_id)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)

            if (account != null && account.idToken != null) {
                viewModel.onIntent(AuthIntent.LoginWithGoogleIdToken(
                    googleIdToken = account.idToken.orEmpty(),
                    onSuccess = {
                        onNavigateToMap()
                    },
                    onNeedSignUp = { firebaseIdToken ->
                        onNavigateToSignUp(firebaseIdToken)
                    },
                    onFailure = {}
                ))
            } else {
                viewModel.onIntent(AuthIntent.ShowError("구글 ID 토큰을 가져오지 못했습니다."))
            }

        } catch (e: ApiException) {
            viewModel.onIntent(AuthIntent.ShowError("구글 로그인 실패 (${e.statusCode})"))
        }
    }

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(serverClientId)
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    LoginContent(
        isLoading = authState.isLoading,
        errorMessage = authState.error,
        onGoogleSignInClick = {
            val availability = GoogleApiAvailability.getInstance()
            val status = availability.isGooglePlayServicesAvailable(context)
            if (status == ConnectionResult.SUCCESS) {
                val intent = googleSignInClient.signInIntent
                launcher.launch(intent)
            } else {
                viewModel.onIntent(AuthIntent.ShowError("Google Play 서비스를 사용할 수 없습니다. ($status)"))
            }
        }
    )
}

@Composable
fun LoginContent(
    isLoading: Boolean,
    errorMessage: String?,
    onGoogleSignInClick: () -> Unit
) {
    val colors = ArchiThemeImpl.archiColor
    SystemBackgroundImageEffect(R.drawable.login_bg)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 로고 이미지
            Image(
                painter = painterResource(id = R.drawable.jjongle_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(500.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 로그인 버튼
            if (isLoading) {
                CircularProgressIndicator(color = colors.contentAccent)
            } else {
                IconButton(
                    onClick = onGoogleSignInClick,
                    modifier = Modifier
                        .size(56.dp)
                        .background(colors.bgDefaultLevel0, shape = CircleShape)
                        .border(1.dp, colors.borderDefaultLevel0, shape = CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = "Google 로그인",
                        modifier = Modifier.size(58.dp)
                    )
                }
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                ArchiText(
                    text = it,
                    style = ArchiThemeImpl.typeScale.textRegularM,
                    color = colors.contentDanger
                )
            }
        }
    }
}
