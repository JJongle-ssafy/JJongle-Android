package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Presentation Residue Contract Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class PresentationResidueContractTest {

    @Test
    fun presentation_production_sources_do_not_keep_temporary_todo_or_legacy_residue() {
        val root = repositoryRoot()
        val sourceRoots = listOf(
            "app/src/main/java",
            "main/presentation/src/main/java",
            "common/presentation/src/main/kotlin",
            "oxgame/presentation/src/main/java",
            "tangram/presentation/src/main/java",
        ).map(root::resolve).filter(Files::exists)

        val forbidden = Regex("""(?i)\b(todo|legacy)\b|레거시|임시|test용""")
        val offenders = sourceRoots.flatMap { sourceRoot ->
            Files.walk(sourceRoot).use { paths ->
                paths
                    .filter { Files.isRegularFile(it) && it.toString().endsWith(".kt") }
                    .filter { forbidden.containsMatchIn(it.readText()) }
                    .map { root.relativize(it).toString() }
                    .toList()
            }
        }

        assertTrue(
            "Presentation production sources must not keep temporary TODO/legacy residue: $offenders",
            offenders.isEmpty(),
        )
    }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
}
