package com.ssafy.jjongle.common.presentation.jank

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun JankScrollWatcher(
    pageName: String,
    scrollableState: ScrollableState,
    reporter: JankReporter? = null,
) {
    val activeReporter = reporter ?: LocalJankReporter.current
    val scrollSession = remember(scrollableState) { ScrollSessionState() }

    LaunchedEffect(pageName, scrollableState, activeReporter) {
        snapshotFlow { scrollableState.isScrollInProgress }
            .distinctUntilChanged()
            .map { isInProgress ->
                if (isInProgress) {
                    scrollSession.hasScrolled = true
                }
                isInProgress
            }
            .drop(1)
            .filter { isInProgress -> !isInProgress && scrollSession.hasScrolled }
            .collect {
                scrollSession.hasScrolled = false
                activeReporter.flush(JankReportReason.SCROLL_END, pageName)
            }
    }
}

/**
 * ScrollSessionState 화면이 구독하는 상태 모델입니다.
 *
 * - 계층: common/presentation
 * - 책임: 렌더링에 필요한 값을 한곳에 모아 UI와 상태 변경 로직을 분리합니다.
 */
private class ScrollSessionState {
    var hasScrolled: Boolean = false
}
