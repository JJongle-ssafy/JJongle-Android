package com.ssafy.jjongle.data.architecture

import java.nio.file.Files
import java.nio.file.Path
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * CommonDataExtraction의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
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
