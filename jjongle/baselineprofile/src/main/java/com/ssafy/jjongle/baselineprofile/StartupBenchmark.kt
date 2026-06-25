package com.ssafy.jjongle.baselineprofile

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * StartupBenchmark 기준 프로파일 수집 시나리오를 정의합니다.
 *
 * - 계층: baselineprofile
 * - 책임: 앱 시작과 주요 경로의 런타임 최적화 데이터를 생성합니다.
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun coldStartup() {
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(StartupTimingMetric()),
            compilationMode = CompilationMode.Partial(),
            startupMode = StartupMode.COLD,
            iterations = 5,
            setupBlock = {
                pressHome()
            },
        ) {
            startActivityAndWait()
            device.waitForIdle()
            if (device.wait(Until.hasObject(By.res(PACKAGE_NAME, "map_mypage_panel")), TIMEOUT_MS)) {
                device.findObject(By.res(PACKAGE_NAME, "map_mypage_panel")).click()
                device.waitForIdle()
            }
        }
    }

    private companion object {
        const val PACKAGE_NAME = "com.ssafy.jjongle"
        const val TIMEOUT_MS = 5_000L
    }
}
