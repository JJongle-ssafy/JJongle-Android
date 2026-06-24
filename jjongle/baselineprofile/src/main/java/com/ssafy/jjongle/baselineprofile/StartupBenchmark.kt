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
