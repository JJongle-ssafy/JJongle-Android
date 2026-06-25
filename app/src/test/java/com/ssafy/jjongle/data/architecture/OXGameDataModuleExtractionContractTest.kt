package com.ssafy.jjongle.data.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * OXGameDataModuleExtraction의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class OXGameDataModuleExtractionContractTest {

    @Test
    fun ox_game_data_lives_in_feature_data_module_not_app_data() {
        val root = repositoryRoot()
        val settings = root.resolve("settings.gradle.kts").readText()

        listOf(
            ":oxgame:entity",
            ":oxgame:domain",
            ":oxgame:data",
            ":oxgame:presentation",
        ).forEach { module ->
            assertTrue(
                "settings.gradle.kts must include $module for feature 4-layer architecture",
                settings.contains("include(\"$module\")"),
            )
        }

        val appDataRoot = root.resolve("app/src/main/java/com/ssafy/jjongle/data")
        val forbiddenAppFiles = listOf(
            "game/LocalOXGameEngine.kt",
            "local/oxgame/OXGameHistoryDao.kt",
            "local/oxgame/OXGameHistoryEntity.kt",
            "local/oxgame/OXGameHistoryWithNotes.kt",
            "local/oxgame/OXWrongAnswerNoteEntity.kt",
            "repository/LocalOXQuizRepositoryImpl.kt",
            "repository/OXGameHistoryRepositoryImpl.kt",
            "repository/OXGameRepositoryImpl.kt",
        )

        forbiddenAppFiles.forEach { relativePath ->
            assertFalse(
                "OXGame data implementation must not remain in :app: $relativePath",
                Files.exists(appDataRoot.resolve(relativePath)),
            )
        }

        val featureDataRoot = root.resolve("oxgame/data/src/main/java/com/ssafy/jjongle/oxgame/data")
        val expectedFeatureFiles = listOf(
            "game/LocalOXGameEngine.kt",
            "local/OXGameHistoryDao.kt",
            "local/OXGameHistoryEntity.kt",
            "local/OXGameHistoryWithNotes.kt",
            "local/OXWrongAnswerNoteEntity.kt",
            "repository/LocalOXQuizRepositoryImpl.kt",
            "repository/OXGameHistoryRepositoryImpl.kt",
            "repository/OXGameRepositoryImpl.kt",
        )

        expectedFeatureFiles.forEach { relativePath ->
            assertTrue(
                "OXGame data implementation must live in :oxgame:data: $relativePath",
                Files.exists(featureDataRoot.resolve(relativePath)),
            )
        }
    }

    @Test
    fun ox_game_local_storage_maps_to_vo_not_legacy_domain_naming() {
        val root = repositoryRoot()
        val repositoryInterface = root.resolve(
            "oxgame/domain/src/main/kotlin/com/ssafy/jjongle/oxgame/domain/repository/OXGameHistoryRepository.kt",
        ).readText()
        val page = root.resolve(
            "oxgame/domain/src/main/kotlin/com/ssafy/jjongle/oxgame/domain/repository/OXGameHistoryPage.kt",
        ).readText()
        val historyEntity = root.resolve(
            "oxgame/data/src/main/java/com/ssafy/jjongle/oxgame/data/local/OXGameHistoryEntity.kt",
        ).readText()
        val noteEntity = root.resolve(
            "oxgame/data/src/main/java/com/ssafy/jjongle/oxgame/data/local/OXWrongAnswerNoteEntity.kt",
        ).readText()
        val repository = root.resolve(
            "oxgame/data/src/main/java/com/ssafy/jjongle/oxgame/data/repository/OXGameHistoryRepositoryImpl.kt",
        ).readText()

        assertTrue(page.contains("import kotlinx.collections.immutable.ImmutableList"))
        assertTrue(page.contains("val content: ImmutableList<OXGameHistory>"))
        assertTrue(repositoryInterface.contains("import kotlinx.collections.immutable.ImmutableList"))
        assertTrue(repositoryInterface.contains("suspend fun getHistoryDetail(historyId: Long): ImmutableList<OXGameWrongAnswerNote>"))
        assertTrue(historyEntity.contains("fun OXGameHistoryEntity.toVO(): OXGameHistory"))
        assertTrue(noteEntity.contains("fun OXWrongAnswerNoteEntity.toVO(): OXGameWrongAnswerNote"))
        assertTrue(noteEntity.contains("fun OXGameWrongAnswerNote.toEntity(historyId: Long): OXWrongAnswerNoteEntity"))
        assertFalse(historyEntity.contains("toDomain"))
        assertFalse(noteEntity.contains("toDomain"))
        assertFalse(repository.contains("toDomain"))
        assertTrue(repository.contains("import com.ssafy.jjongle.oxgame.data.local.toVO"))
        assertTrue(repository.contains("histories.map { it.history.toVO() }"))
        assertTrue(repository.contains(".map { it.toVO() }"))
        assertTrue(repository.contains(".toPersistentList()"))
    }

    @Test
    fun ox_game_entity_and_domain_contracts_are_feature_owned_not_common_owned() {
        val root = repositoryRoot()
        val entityNames = listOf(
            "GameConnectionState",
            "GameEvent",
            "GameStartEvent",
            "GameErrorEvent",
            "GameFinishEvent",
            "GameProfileImage",
            "GameScore",
            "OX",
            "OXGameHistory",
            "OXGameWrongAnswerNote",
            "OXScoreUpdate",
            "Quiz",
            "QuizResult",
            "QuizSession",
            "SubmitResultEvent",
            "UnknownGameEvent",
            "UserPosition",
        )
        val repositoryNames = listOf(
            "OXGameRepository",
            "OXQuizRepository",
            "OXGameHistoryRepository",
            "OXGameHistoryPage",
        )
        val useCaseNames = listOf(
            "GameActionUseCase",
            "StartOXGameUseCase",
            "UpdateOXScoreUseCase",
            "CalculateOXRankingsUseCase",
            "GetOXGameHistoriesUseCase",
            "GetOXGameHistoryDetailUseCase",
        )

        entityNames.forEach { name ->
            assertFalse(
                "OX feature entity must not remain in common: $name",
                Files.exists(root.resolve("common/entity/src/main/kotlin/com/ssafy/jjongle/common/entity/$name.kt")),
            )
            val featureSource = root.resolve("oxgame/entity/src/main/kotlin/com/ssafy/jjongle/oxgame/entity/$name.kt")
            assertTrue("OX feature entity must live in :oxgame:entity: $name", Files.exists(featureSource))
            assertTrue(featureSource.readText().contains("package com.ssafy.jjongle.oxgame.entity"))
        }

        repositoryNames.forEach { name ->
            assertFalse(
                "OX feature repository contract must not remain in common: $name",
                Files.exists(root.resolve("common/domain/src/main/kotlin/com/ssafy/jjongle/common/domain/repository/$name.kt")),
            )
            val featureSource =
                root.resolve("oxgame/domain/src/main/kotlin/com/ssafy/jjongle/oxgame/domain/repository/$name.kt")
            assertTrue("OX repository contract must live in :oxgame:domain: $name", Files.exists(featureSource))
            assertTrue(featureSource.readText().contains("package com.ssafy.jjongle.oxgame.domain.repository"))
        }

        useCaseNames.forEach { name ->
            assertFalse(
                "OX feature usecase must not remain in common: $name",
                Files.exists(root.resolve("common/domain/src/main/kotlin/com/ssafy/jjongle/common/domain/usecase/$name.kt")),
            )
            val featureSource =
                root.resolve("oxgame/domain/src/main/kotlin/com/ssafy/jjongle/oxgame/domain/usecase/$name.kt")
            assertTrue("OX usecase must live in :oxgame:domain: $name", Files.exists(featureSource))
            assertTrue(featureSource.readText().contains("package com.ssafy.jjongle.oxgame.domain.usecase"))
        }
    }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
}
