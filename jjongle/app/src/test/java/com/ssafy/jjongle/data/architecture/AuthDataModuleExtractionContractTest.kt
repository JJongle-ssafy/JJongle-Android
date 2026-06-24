package com.ssafy.jjongle.data.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthDataModuleExtractionContractTest {

    @Test
    fun auth_data_lives_in_common_data_not_app_data() {
        val root = repositoryRoot()
        val appDataRoot = root.resolve("app/src/main/java/com/ssafy/jjongle/data")
        val forbiddenAppFiles = listOf(
            "firebase/FirebaseAuthDataSource.kt",
            "firebase/FirebaseAuthDataSourceImpl.kt",
            "firebase/FirebaseAuthenticatedUser.kt",
            "firebase/FirestoreUserProfileDataSource.kt",
            "firebase/UserProfileDataSource.kt",
            "firebase/model/UserProfileDto.kt",
            "local/AuthDataSource.kt",
            "repository/AuthRepositoryImpl.kt",
            "service/GoogleAuthServiceImpl.kt",
        )

        forbiddenAppFiles.forEach { relativePath ->
            assertFalse(
                "Auth data implementation must not remain in :app: $relativePath",
                Files.exists(appDataRoot.resolve(relativePath)),
            )
        }

        val commonDataRoot = root.resolve("common/data/src/main/java/com/ssafy/jjongle/common/data")
        val expectedCommonFiles = listOf(
            "firebase/FirebaseAuthDataSource.kt",
            "firebase/FirebaseAuthDataSourceImpl.kt",
            "firebase/FirebaseAuthenticatedUser.kt",
            "firebase/FirestoreUserProfileDataSource.kt",
            "firebase/UserProfileDataSource.kt",
            "firebase/model/UserProfileDto.kt",
            "local/AuthDataSource.kt",
            "repository/AuthRepositoryImpl.kt",
            "service/GoogleAuthServiceImpl.kt",
        )

        expectedCommonFiles.forEach { relativePath ->
            assertTrue(
                "Auth data implementation must live in :common:data: $relativePath",
                Files.exists(commonDataRoot.resolve(relativePath)),
            )
        }
    }

    @Test
    fun auth_firebase_profile_dto_uses_nullable_fields_and_to_vo_boundary() {
        val root = repositoryRoot()
        val dto = root.resolve(
            "common/data/src/main/java/com/ssafy/jjongle/common/data/firebase/model/UserProfileDto.kt",
        ).readText()
        val userInfo = root.resolve(
            "common/entity/src/main/kotlin/com/ssafy/jjongle/common/entity/UserInfo.kt",
        ).readText()
        val repository = root.resolve(
            "common/data/src/main/java/com/ssafy/jjongle/common/data/repository/AuthRepositoryImpl.kt",
        ).readText()

        assertTrue(dto.contains("data class UserProfileDto("))
        assertTrue(dto.contains("val nickname: String? = null"))
        assertTrue(dto.contains("val profileImage: String? = null"))
        assertTrue(dto.contains("val email: String? = null"))
        assertTrue(dto.contains("fun UserProfileDto.toVO("))
        assertFalse(dto.contains("fun UserProfileDto.toDomain("))
        assertTrue(userInfo.contains("val userId: Long ="))
        assertTrue(userInfo.contains("val email: String ="))
        assertFalse(userInfo.contains("val email: String?"))
        assertTrue(userInfo.contains("val empty = UserInfo()"))
        assertTrue(dto.contains("?: UserInfo.MISSING_EMAIL"))
        assertTrue(repository.contains(".model.toVO"))
        assertTrue(repository.contains("val user = toVO("))
        assertFalse(repository.contains(".model.toDomain"))
    }

    @Test
    fun entity_vo_collections_use_immutable_lists_not_raw_lists() {
        val root = repositoryRoot()
        val entitySources = Files.walk(
            root.resolve("common/entity/src/main/kotlin/com/ssafy/jjongle/common/entity"),
        )
            .filter { Files.isRegularFile(it) && it.toString().endsWith(".kt") }
            .toList()

        assertTrue("Expected common entity sources", entitySources.isNotEmpty())
        entitySources.forEach { path ->
            val source = path.readText()
            val relative = root.relativize(path)
            assertFalse("$relative must not expose raw List fields", source.contains(Regex("""val\s+\w+\s*:\s*List<""")))
            assertFalse("$relative must not use emptyList() as VO default", source.contains("emptyList()"))
            if (source.contains("ImmutableList<")) {
                assertTrue("$relative must import ImmutableList", source.contains("kotlinx.collections.immutable.ImmutableList"))
            }
        }
    }

    @Test
    fun tangram_server_vos_expose_defaults_and_companion_empty() {
        val root = repositoryRoot()
        val history = root.resolve(
            "tangram/entity/src/main/kotlin/com/ssafy/jjongle/tangram/entity/TangramHistory.kt",
        ).readText()
        val detail = root.resolve(
            "tangram/entity/src/main/kotlin/com/ssafy/jjongle/tangram/entity/TangramDetail.kt",
        ).readText()
        val page = root.resolve(
            "tangram/entity/src/main/kotlin/com/ssafy/jjongle/tangram/entity/TangramHistoriesPage.kt",
        ).readText()

        listOf("stage: Int =", "tangramId: Long =", "animal: AnimalType =").forEach { token ->
            assertTrue("TangramHistory must provide default for $token", history.contains(token))
        }
        assertTrue(history.contains("companion object"))
        assertTrue(history.contains("val empty = TangramHistory()"))

        listOf("tangramId: Long =", "animal: AnimalType =", "story: String =").forEach { token ->
            assertTrue("TangramDetail must provide default for $token", detail.contains(token))
        }
        assertTrue(detail.contains("companion object"))
        assertTrue(detail.contains("val empty = TangramDetail()"))

        assertTrue(page.contains("val empty = TangramHistoriesPage()"))
    }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
}
