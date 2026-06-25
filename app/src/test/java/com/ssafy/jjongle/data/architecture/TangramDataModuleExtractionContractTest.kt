package com.ssafy.jjongle.data.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tangram Data Module Extraction Contract Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class TangramDataModuleExtractionContractTest {

    @Test
    fun tangram_data_lives_in_feature_data_module_not_app_data() {
        val root = repositoryRoot()
        val settings = root.resolve("settings.gradle.kts").readText()

        listOf(
            ":tangram:entity",
            ":tangram:domain",
            ":tangram:data",
            ":tangram:presentation",
        ).forEach { module ->
            assertTrue(
                "settings.gradle.kts must include $module for feature 4-layer architecture",
                settings.contains("include(\"$module\")"),
            )
        }

        val appDataRoot = root.resolve("app/src/main/java/com/ssafy/jjongle/data")
        val forbiddenAppFiles = listOf(
            "remote/TangramGameApiService.kt",
            "remote/TangramGameRemoteDataSource.kt",
            "remote/model/SingleGameResponse.kt",
            "remote/model/TangramDetailResponse.kt",
            "remote/model/TangramHistoriesPageResponse.kt",
            "remote/model/TangramHistoryItemDto.kt",
            "repository/TangramGameRepositoryImpl.kt",
        )

        forbiddenAppFiles.forEach { relativePath ->
            assertFalse(
                "Tangram data implementation must not remain in :app: $relativePath",
                Files.exists(appDataRoot.resolve(relativePath)),
            )
        }

        val featureDataRoot = root.resolve("tangram/data/src/main/java/com/ssafy/jjongle/tangram/data")
        val expectedFeatureFiles = listOf(
            "remote/TangramGameApiService.kt",
            "remote/TangramGameRemoteDataSource.kt",
            "remote/model/SingleGameDTO.kt",
            "remote/model/TangramDetailDTO.kt",
            "remote/model/TangramHistoriesPageDTO.kt",
            "remote/model/TangramHistoryItemDTO.kt",
            "repository/TangramGameRepositoryImpl.kt",
        )

        expectedFeatureFiles.forEach { relativePath ->
            assertTrue(
                "Tangram data implementation must live in :tangram:data: $relativePath",
                Files.exists(featureDataRoot.resolve(relativePath)),
            )
        }
    }

    @Test
    fun tangram_entity_and_domain_contracts_are_feature_owned_not_common_owned() {
        val root = repositoryRoot()

        val forbiddenCommonFiles = listOf(
            "common/entity/src/main/kotlin/com/ssafy/jjongle/common/entity/TangramDetail.kt",
            "common/entity/src/main/kotlin/com/ssafy/jjongle/common/entity/TangramHistory.kt",
            "common/entity/src/main/kotlin/com/ssafy/jjongle/common/entity/TangramHistoriesPage.kt",
            "common/domain/src/main/kotlin/com/ssafy/jjongle/common/domain/repository/TangramGameRepository.kt",
            "common/domain/src/main/kotlin/com/ssafy/jjongle/common/domain/usecase/GetTangramDetailUseCase.kt",
            "common/domain/src/main/kotlin/com/ssafy/jjongle/common/domain/usecase/GetTangramHistoriesUseCase.kt",
            "common/domain/src/main/kotlin/com/ssafy/jjongle/common/domain/usecase/TangramGameUseCase.kt",
        )

        forbiddenCommonFiles.forEach { relativePath ->
            assertFalse(
                "Tangram feature-owned contract must not remain in common: $relativePath",
                Files.exists(root.resolve(relativePath)),
            )
        }

        val featureFiles = mapOf(
            "tangram/entity/src/main/kotlin/com/ssafy/jjongle/tangram/entity/TangramDetail.kt" to
                "package com.ssafy.jjongle.tangram.entity",
            "tangram/entity/src/main/kotlin/com/ssafy/jjongle/tangram/entity/TangramHistory.kt" to
                "package com.ssafy.jjongle.tangram.entity",
            "tangram/entity/src/main/kotlin/com/ssafy/jjongle/tangram/entity/TangramHistoriesPage.kt" to
                "package com.ssafy.jjongle.tangram.entity",
            "tangram/domain/src/main/kotlin/com/ssafy/jjongle/tangram/domain/repository/TangramGameRepository.kt" to
                "package com.ssafy.jjongle.tangram.domain.repository",
            "tangram/domain/src/main/kotlin/com/ssafy/jjongle/tangram/domain/usecase/GetTangramDetailUseCase.kt" to
                "package com.ssafy.jjongle.tangram.domain.usecase",
            "tangram/domain/src/main/kotlin/com/ssafy/jjongle/tangram/domain/usecase/GetTangramHistoriesUseCase.kt" to
                "package com.ssafy.jjongle.tangram.domain.usecase",
            "tangram/domain/src/main/kotlin/com/ssafy/jjongle/tangram/domain/usecase/TangramGameUseCase.kt" to
                "package com.ssafy.jjongle.tangram.domain.usecase",
        )

        featureFiles.forEach { (relativePath, packageDeclaration) ->
            val source = root.resolve(relativePath)
            assertTrue("Tangram feature contract must live in $relativePath", Files.exists(source))
            assertTrue(
                "$relativePath must declare the feature-owned package",
                source.readText().contains(packageDeclaration),
            )
        }
    }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
}
