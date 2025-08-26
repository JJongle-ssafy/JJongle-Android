package com.ssafy.jjongle.presentation.ui.screen


import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.ssafy.jjongle.R
import com.ssafy.jjongle.presentation.viewmodel.AuthViewModel

@Composable
fun LoginScreen(

    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToMap: () -> Unit,
    onNavigateToSignUp: (String) -> Unit,   // 회원가입 화면으로 이동하는 콜백

) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("LoginScreen", "ActivityResult 콜백 진입") // ActivityResult 콜백 로그

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d("LoginScreen", "account: $account")
            Log.d("LoginScreen", "idToken: ${account?.idToken}")

            if (account != null && account.idToken != null) {

                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            FirebaseAuth.getInstance().currentUser?.getIdToken(true)
                                ?.addOnSuccessListener { result ->
                                    val firebaseIdToken = result.token
                                    Log.d("FirebaseIDToken", firebaseIdToken!!) // ✅ 서버에 이걸 보내야 함

                                    viewModel.login(
                                        idToken = firebaseIdToken,
                                        onSuccess = {
                                            onNavigateToMap()
                                        },
                                        onNeedSignUp = {
                                            Log.d("LoginScreen", "🟡 onNeedSignUp 호출됨")

                                            onNavigateToSignUp(firebaseIdToken)  // 회원가입 화면으로 이동
                                        },
                                        onFailure = {
                                            Toast.makeText(context, "로그인 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    )

                                }
                        }
                    }
            }

        } catch (e: ApiException) {
            Log.e("LoginScreen", "구글 로그인 실패", e) // ✅ 반드시 필요

            Toast.makeText(context, "구글 로그인 실패", Toast.LENGTH_SHORT).show()
        }
    }

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.login_bg),
            contentDescription = "Login Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

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
            if (authState.isLoading) {
                Log.d("LoginScreen", "⏳ 로딩 중")
                CircularProgressIndicator()
            } else {
                IconButton(
                    onClick = {
                        Log.d("LoginScreen", "로그인 버튼 클릭됨")
                        val intent = googleSignInClient.signInIntent
                        launcher.launch(intent)
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.White, shape = CircleShape)
                        .border(1.dp, Color.LightGray, shape = CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = "Google 로그인",
                        modifier = Modifier.size(58.dp)
                    )
                }
            }

            authState.error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = it, color = Color.Red)
            }
        }
    }
}
