package com.ssafy.jjongle.data.architecture

import java.nio.file.Files
import java.nio.file.Path
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CommonDataExtractionContractTest {

    @Test
    fun settings_repository_impl_lives_in_common_data_not_app_data() {
        val root = repositoryRoot()

        assertFalse(
            "SettingsRepositoryImpl is a common data implementation and must not remain in :app data",
            Files.exists(root.resolve("app/src/main/java/com/ssafy/jjongle/data/repository/SettingsRepositoryImpl.kt")),
        )
        assertTrue(
            "SettingsRepositoryImpl must live in :common:data",
            Files.exists(
                root.resolve(
                    "common/data/src/main/java/com/ssafy/jjongle/common/data/repository/SettingsRepositoryImpl.kt"
                )
            ),
        )
    }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
}
