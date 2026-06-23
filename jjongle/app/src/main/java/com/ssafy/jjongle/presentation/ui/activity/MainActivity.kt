package com.ssafy.jjongle.presentation.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.ssafy.jjongle.presentation.navigation.NavGraph
import com.ssafy.jjongle.presentation.navigation.Screen
import com.ssafy.jjongle.presentation.ui.layout.DesignCanvas
import com.ssafy.jjongle.presentation.ui.theme.JjongleTheme
import com.ssafy.jjongle.presentation.viewmodel.NavigationViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.viewmodel.compose.viewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            JjongleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navigationViewModel: NavigationViewModel = viewModel()
                    val currentRoute by navigationViewModel.currentRoute.collectAsState()

                    val navController = rememberNavController()

                    Log.d("MainActivity", "Current Route: $currentRoute")

                    val bgState = remember { mutableStateOf<Int?>(null) }

                    CompositionLocalProvider(
                        com.ssafy.jjongle.presentation.ui.layout.LocalLetterboxImageResController provides bgState
                    ) {
                        DesignCanvas(modifier = Modifier.fillMaxSize()) {
                            NavGraph(
                                navController = navController,
                                startDestination = Screen.Splash.route,
    //                        startDestination = Screen.Camera.route,
                                navigationViewModel = navigationViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
