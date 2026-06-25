package com.ssafy.jjongle.presentation.navigation

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * AppLinkManifest의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class AppLinkManifestContractTest {

    @Test
    fun main_activity_declares_https_app_link_filter_for_registered_route_paths() {
        val manifest = repositoryRoot()
            .resolve("app/src/main/AndroidManifest.xml")
            .readText()

        assertTrue(manifest.contains("""android:autoVerify="true""""))
        assertTrue(manifest.contains("""<action android:name="android.intent.action.VIEW" />"""))
        assertTrue(manifest.contains("""<category android:name="android.intent.category.BROWSABLE" />"""))
        assertTrue(manifest.contains("""android:scheme="https""""))
        assertTrue(manifest.contains("""android:host="www.androidarchi.com""""))
        assertTrue(manifest.contains("""android:pathPrefix="/""""))
    }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
}
