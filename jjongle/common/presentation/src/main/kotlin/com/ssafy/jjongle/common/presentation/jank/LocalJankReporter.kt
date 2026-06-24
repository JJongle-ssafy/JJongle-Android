package com.ssafy.jjongle.common.presentation.jank

import androidx.compose.runtime.staticCompositionLocalOf

val LocalJankReporter = staticCompositionLocalOf<JankReporter> { DebugJankReporter }
