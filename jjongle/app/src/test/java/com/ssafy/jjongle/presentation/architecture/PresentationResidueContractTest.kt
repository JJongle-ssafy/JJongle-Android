package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * PresentationResidue의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
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
