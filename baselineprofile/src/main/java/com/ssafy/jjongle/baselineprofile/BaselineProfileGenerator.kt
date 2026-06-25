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
 * BaselineProfileGenerator 기준 프로파일 수집 시나리오를 정의합니다.
 *
 * - 계층: baselineprofile
 * - 책임: 앱 시작과 주요 경로의 런타임 최적화 데이터를 생성합니다.
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
