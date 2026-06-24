package com.ssafy.jjongle.data.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppDiResidueExtractionContractTest {

    @Test
    fun data_and_navigation_bindings_do_not_live_in_app_di() {
        val root = repositoryRoot()
        val appDiRoot = root.resolve("app/src/main/java/com/ssafy/jjongle/di")

        listOf(
            "NetworkModule.kt",
            "RepositoryModule.kt",
        ).forEach { fileName ->
            assertFalse(
                "Data infrastructure DI must not remain in :app: $fileName",
                Files.exists(appDiRoot.resolve(fileName)),
            )
        }

        val appModuleSource = appDiRoot.resolve("AppModule.kt").readText()
        listOf(
            "SharedPreferences",
            "SettingsRepositoryImpl",
            "SettingsRepository",
            "AppNavigationHelper",
            "NavigationHelper",
        ).forEach { forbidden ->
            assertFalse(
                "AppModule must not bind data/navigation implementation detail: $forbidden",
                appModuleSource.contains(forbidden),
            )
        }

        assertTrue(
            "NetworkModule must live in :common:data",
            Files.exists(
                root.resolve(
                    "common/data/src/main/java/com/ssafy/jjongle/common/data/di/NetworkModule.kt"
                )
            ),
        )
        assertTrue(
            "SharedPreferences and shared repository bindings must live in :common:data",
            Files.exists(
                root.resolve(
                    "common/data/src/main/java/com/ssafy/jjongle/common/data/di/StorageDataModule.kt"
                )
            ),
        )
        assertTrue(
            "NavigationHelper binding must live with main presentation navigation",
            Files.exists(
                root.resolve(
                    "main/presentation/src/main/java/com/ssafy/jjongle/presentation/navigation/NavigationModule.kt"
                )
            ),
        )

        val appBuildGradle = root.resolve("app/build.gradle.kts").readText()
        listOf(
            "libs.retrofit",
            "libs.okhttp",
            "libs.firebase",
            "libs.androidx.camera",
            "com.google.mlkit",
            "lottie-compose",
        ).forEach { forbiddenDependency ->
            assertFalse(
                ":app must not directly depend on data/feature implementation dependency: $forbiddenDependency",
                appBuildGradle.contains(forbiddenDependency),
            )
        }
    }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
}
