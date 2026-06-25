package com.ssafy.jjongle.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Baseline Profile Generator는 앱 시작 성능을 측정하거나 baseline profile 생성을 자동화합니다.
 *
 * 프로필 기반 최적화가 깨지지 않도록 실제 앱 실행 경로를 테스트 코드로 기록합니다.
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() {
        baselineProfileRule.collect(
            packageName = PACKAGE_NAME,
        ) {
            pressHome()
            startActivityAndWait()
            device.waitForIdle()
            waitAndClick("map_mypage_panel")
            waitAndClick("mypage_animal_book_button")
            device.waitForIdle()
            device.findObject(By.res(PACKAGE_NAME, "animal_book_item_BEAR"))?.click()
            device.waitForIdle()
            device.findObject(By.res(PACKAGE_NAME, "animal_detail_camera_button"))?.click()
            device.waitForIdle()
        }
    }

    private fun MacrobenchmarkScope.waitAndClick(resourceId: String) {
        val selector = By.res(PACKAGE_NAME, resourceId)
        check(device.wait(Until.hasObject(selector), TIMEOUT_MS)) {
            "Unable to find $resourceId during baseline profile collection"
        }
        device.findObject(selector).click()
    }

    private companion object {
        const val PACKAGE_NAME = "com.ssafy.jjongle"
        const val TIMEOUT_MS = 5_000L
    }
}
