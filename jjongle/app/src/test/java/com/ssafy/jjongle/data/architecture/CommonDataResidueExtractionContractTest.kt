package com.ssafy.jjongle.data.architecture

import java.nio.file.Files
import java.nio.file.Path
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CommonDataResidueExtractionContractTest {

    @Test
    fun shared_database_and_feature_navigation_state_repository_do_not_remain_in_app_data() {
        val root = repositoryRoot()

        listOf(
            "local/JjongleDatabase.kt",
            "repository/NavigationStateRepositoryImpl.kt",
            "service/BgmManager.kt",
        ).forEach { relativePath ->
            assertFalse(
                "Shared data implementation must not remain in :app: $relativePath",
                Files.exists(root.resolve("app/src/main/java/com/ssafy/jjongle/data/$relativePath")),
            )
        }

        assertTrue(
            "NavigationStateRepositoryImpl must live with the main navigation shell data module",
            Files.exists(
                root.resolve(
                    "main/data/src/main/java/com/ssafy/jjongle/main/data/repository/NavigationStateRepositoryImpl.kt"
                )
            ),
        )
        assertFalse(
            "NavigationStateRepositoryImpl must not remain in :common:data after main:data extraction",
            Files.exists(
                root.resolve(
                    "common/data/src/main/java/com/ssafy/jjongle/common/data/repository/NavigationStateRepositoryImpl.kt"
                )
            ),
        )
        assertTrue(
            "OX-only Room database must live with OXGame data implementation",
            Files.exists(
                root.resolve(
                    "oxgame/data/src/main/java/com/ssafy/jjongle/oxgame/data/local/JjongleDatabase.kt"
                )
            ),
        )
        assertTrue(
            "BgmManager must live in :common:data with its raw audio resources",
            Files.exists(
                root.resolve(
                    "common/data/src/main/java/com/ssafy/jjongle/common/data/service/BgmManager.kt"
                )
            ),
        )

        val appDataRoot = root.resolve("app/src/main/java/com/ssafy/jjongle/data")
        if (Files.exists(appDataRoot)) {
            val remainingFiles = Files.walk(appDataRoot).use { paths ->
                paths
                    .filter(Files::isRegularFile)
                    .map { appDataRoot.relativize(it).toString() }
                    .toList()
            }
            assertTrue(
                "app data package must be empty after data implementations move to modules: $remainingFiles",
                remainingFiles.isEmpty(),
            )
        }
    }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
}
