package com.ssafy.jjongle.presentation.navigation

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertTrue
import org.junit.Test

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
