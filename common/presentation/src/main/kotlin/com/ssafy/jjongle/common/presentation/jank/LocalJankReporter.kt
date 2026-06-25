package com.ssafy.jjongle.common.presentation.jank

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * LocalJankReporter에서 공유하는 최상위 값을 제공합니다.
 *
 * - 계층: common/presentation
 * - 책임: 모듈 내부에서 반복 사용되는 설정이나 상태 기준을 한곳에 둡니다.
 */
val LocalJankReporter = staticCompositionLocalOf<JankReporter> { DebugJankReporter }
