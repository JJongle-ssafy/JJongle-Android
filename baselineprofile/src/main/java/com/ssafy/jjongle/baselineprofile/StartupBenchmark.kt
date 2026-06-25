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
 * Startup Benchmark는 앱 시작 성능을 측정하거나 baseline profile 생성을 자동화합니다.
 *
 * 프로필 기반 최적화가 깨지지 않도록 실제 앱 실행 경로를 테스트 코드로 기록합니다.
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
