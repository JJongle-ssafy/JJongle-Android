package com.ssafy.jjongle.data.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.readText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * CommonBoundary의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class CommonBoundaryContractTest {

    @Test
    fun common_entity_contains_only_shared_cross_feature_value_objects() {
        val root = repositoryRoot()
        val commonEntityRoot = root.resolve("common/entity/src/main/kotlin/com/ssafy/jjongle/common/entity")
        val actualFiles = Files.walk(commonEntityRoot).use { paths ->
            paths
                .filter { Files.isRegularFile(it) && it.toString().endsWith(".kt") }
                .map { it.name }
                .sorted()
                .toList()
        }

        assertEquals(
            "common:entity should stay limited to cross-feature/shared app value objects",
            listOf(
                "AnimalType.kt",
                "AuthState.kt",
                "BgmGroup.kt",
                "GoogleUser.kt",
                "UserInfo.kt",
            ),
            actualFiles,
        )

        val forbiddenFeatureTokens = listOf(
            "Tangram",
            "OX",
            "Quiz",
            "GameEvent",
            "GameScore",
            "GameSession",
        )
        actualFiles.forEach { fileName ->
            forbiddenFeatureTokens.forEach { token ->
                assertFalse("feature-owned entity must not return to common: $fileName", fileName.contains(token))
            }
        }
    }

    @Test
    fun common_domain_contains_only_base_errors_helpers_navigation_and_shared_repositories() {
        val root = repositoryRoot()
        val commonDomainRoot = root.resolve("common/domain/src/main/kotlin/com/ssafy/jjongle/common/domain")
        val actualFiles = Files.walk(commonDomainRoot).use { paths ->
            paths
                .filter { Files.isRegularFile(it) && it.toString().endsWith(".kt") }
                .map { commonDomainRoot.relativize(it).toString() }
                .sorted()
                .toList()
        }

        val expectedFiles = listOf(
            "base/BaseUseCase.kt",
            "error/ErrorParser.kt",
            "error/HttpError.kt",
            "error/auth/AuthError.kt",
            "error/auth/AuthException.kt",
            "error/auth/AuthStorageUnavailableError.kt",
            "error/auth/HttpAuthError.kt",
            "error/auth/InvalidRefreshTokenAuthError.kt",
            "error/auth/MissingTokenAuthError.kt",
            "error/auth/UnknownAuthError.kt",
            "error/auth/UserAlreadyExistsAuthError.kt",
            "helper/MessageHelper.kt",
            "helper/NavigationHelper.kt",
            "helper/ResourceHelper.kt",
            "navigation/NavRoute.kt",
            "navigation/NavSignal.kt",
            "repository/AuthRepository.kt",
            "repository/BgmRepository.kt",
            "repository/GoogleAuthService.kt",
            "repository/SettingsRepository.kt",
            "usecase/AuthUseCase.kt",
        ).sorted()

        assertEquals(
            "common:domain should not absorb feature-owned repositories/usecases",
            expectedFiles,
            actualFiles,
        )
    }

    @Test
    fun common_layers_do_not_depend_on_feature_packages() {
        val root = repositoryRoot()
        val commonRoots = listOf(
            root.resolve("common/entity/src/main/kotlin"),
            root.resolve("common/domain/src/main/kotlin"),
            root.resolve("common/data/src/main/java"),
        )
        val offenders = commonRoots
            .flatMap { sourceRoot ->
                Files.walk(sourceRoot).use { paths ->
                    paths
                        .filter { Files.isRegularFile(it) && it.toString().endsWith(".kt") }
                        .toList()
                }
            }
            .flatMap { file ->
                val source = file.readText()
                Regex("""import\s+com\.ssafy\.jjongle\.(main|oxgame|tangram)\.""")
                    .findAll(source)
                    .map { "${root.relativize(file)} imports ${it.value}" }
                    .toList()
            }

        assertTrue(
            "common layers must remain feature-agnostic and must not import main/oxgame/tangram packages: $offenders",
            offenders.isEmpty(),
        )
    }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
}
