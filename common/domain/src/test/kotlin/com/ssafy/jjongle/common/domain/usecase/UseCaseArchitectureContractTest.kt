package com.ssafy.jjongle.common.domain.usecase

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Use Case Architecture Contract Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class UseCaseArchitectureContractTest {

    @Test
    fun repository_backed_use_cases_follow_base_use_case_error_handling_contract() {
        repositoryBackedUseCaseSources().forEach { sourcePath ->
            val source = sourcePath.readText()
            val label = sourcePath.name

            assertTrue("$label must extend BaseUseCase", source.contains(") : BaseUseCase("))
            assertTrue("$label must receive ResourceHelper", source.contains("resourceHelper: ResourceHelper"))
            assertTrue("$label must receive MessageHelper", source.contains("messageHelper: MessageHelper"))
            assertTrue("$label must receive NavigationHelper", source.contains("navigationHelper: NavigationHelper"))
            assertTrue("$label must receive TTIHelper", source.contains("ttiHelper: TTIHelper"))
            assertTrue(
                "$label must pass all helpers to BaseUseCase in order",
                source.contains("BaseUseCase(resourceHelper, messageHelper, navigationHelper, ttiHelper)"),
            )
            assertFalse(
                "$label must not duplicate repository error policy with local try/catch Result wrapping",
                Regex("""(?:=|return)\s*try\s*\{""").containsMatchIn(source),
            )
            assertFalse(
                "$label must not expose raw HttpResponseException handling outside BaseUseCase wrapper",
                Regex("""catch\s*\(\s*\w+\s*:\s*HttpResponseException\s*\)""").containsMatchIn(source),
            )
            assertTrue(
                "$label must route Result-returning repository work through BaseUseCase common wrapper",
                !source.contains("Result<") || source.contains("executeWithCommonHttpHandling"),
            )
        }
    }

    @Test
    fun pure_calculation_use_cases_do_not_depend_on_error_handling_helpers() {
        pureUseCaseSources().forEach { sourcePath ->
            val source = sourcePath.readText()
            val label = sourcePath.name

            assertFalse("$label must not import BaseUseCase", source.contains("BaseUseCase"))
            assertFalse("$label must not import helper dependencies", source.contains(".domain.helper."))
            assertFalse("$label must not import repository dependencies", source.contains(".domain.repository."))
            assertFalse("$label must not return Result", source.contains("Result<"))
        }
    }

    private fun repositoryBackedUseCaseSources(): List<Path> =
        useCaseSources().filter { path ->
            val source = path.readText()
            source.contains(".domain.repository.") || source.contains("Repository")
        }

    private fun pureUseCaseSources(): List<Path> =
        useCaseSources().filter { path ->
            val source = path.readText()
            !source.contains(".domain.repository.") && !source.contains("Repository")
        }

    private fun useCaseSources(): List<Path> {
        val root = repositoryRoot()
        val useCaseRoot = root.resolve(
            "common/domain/src/main/kotlin/com/ssafy/jjongle/common/domain/usecase",
        )
        return Files.walk(useCaseRoot).use { paths ->
            paths
                .filter { Files.isRegularFile(it) && it.name.endsWith("UseCase.kt") }
                .sorted()
                .toList()
        }
    }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
}
