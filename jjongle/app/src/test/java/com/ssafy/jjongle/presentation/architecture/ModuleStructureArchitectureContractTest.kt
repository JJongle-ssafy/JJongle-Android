package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * ModuleStructureArchitecture의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class ModuleStructureArchitectureContractTest {

    @Test
    fun pure_kotlin_modules_are_jvm_17_and_do_not_apply_android_plugins() {
        val root = repositoryRoot()
        val modules = listOf(
            "common/entity",
            "common/domain",
            "main/entity",
            "main/domain",
            "oxgame/entity",
            "oxgame/domain",
            "tangram/entity",
            "tangram/domain",
            "tti",
        )

        modules.forEach { module ->
            val build = root.resolve("$module/build.gradle.kts").readText()
            assertTrue("$module must use kotlin-jvm", build.contains("alias(libs.plugins.kotlin.jvm)"))
            assertTrue("$module must use JVM 17", build.contains("jvmToolchain(17)"))
            assertFalse("$module must stay Android-free", build.contains("android.library"))
            assertFalse("$module must stay Android-free", build.contains("android.application"))
            assertFalse("$module must stay Android-free", build.contains("com.android"))
        }
    }

    @Test
    fun android_modules_target_jvm_17_and_presentation_modules_use_compose_stability_config() {
        val root = repositoryRoot()
        val androidModules = listOf(
            "app",
            "baselineprofile",
            "common/data",
            "common/presentation",
            "main/data",
            "main/presentation",
            "oxgame/data",
            "oxgame/presentation",
            "tangram/data",
            "tangram/presentation",
        )

        androidModules.forEach { module ->
            val build = root.resolve("$module/build.gradle.kts").readText()
            assertTrue("$module must target Java 17", build.contains("sourceCompatibility = JavaVersion.VERSION_17"))
            assertTrue("$module must target Java 17", build.contains("targetCompatibility = JavaVersion.VERSION_17"))
            assertTrue("$module must target Kotlin JVM 17", build.contains("""jvmTarget = "17""""))
        }

        val composeStability = root.resolve("compose_stability.conf").readText()
        assertTrue(composeStability.contains("com.ssafy.jjongle.common.entity.**"))
        assertTrue(composeStability.contains("com.ssafy.jjongle.oxgame.entity.**"))
        assertTrue(composeStability.contains("com.ssafy.jjongle.tangram.entity.**"))

        listOf(
            "app",
            "common/presentation",
            "main/presentation",
            "oxgame/presentation",
            "tangram/presentation",
        ).forEach { module ->
            val build = root.resolve("$module/build.gradle.kts").readText()
            assertTrue("$module must enable Compose", build.contains("compose = true"))
            assertTrue(
                "$module must wire compose_stability.conf",
                build.contains("""stabilityConfigurationFile.set(rootProject.layout.projectDirectory.file("compose_stability.conf"))"""),
            )
        }
    }

    @Test
    fun main_navigation_shell_has_domain_data_and_presentation_layers() {
        val root = repositoryRoot()
        val settings = root.resolve("settings.gradle.kts").readText()
        val appBuild = root.resolve("app/build.gradle.kts").readText()
        val mainEntityBuild = root.resolve("main/entity/build.gradle.kts").readText()
        val mainDomainBuild = root.resolve("main/domain/build.gradle.kts").readText()
        val mainDataBuild = root.resolve("main/data/build.gradle.kts").readText()
        val navigationRepository = root.resolve(
            "main/domain/src/main/kotlin/com/ssafy/jjongle/main/domain/repository/NavigationStateRepository.kt",
        ).readText()
        val navigationRepositoryImpl = root.resolve(
            "main/data/src/main/java/com/ssafy/jjongle/main/data/repository/NavigationStateRepositoryImpl.kt",
        ).readText()
        val mainDataModule = root.resolve(
            "main/data/src/main/java/com/ssafy/jjongle/main/data/di/MainDataModule.kt",
        ).readText()
        val navigationViewModel = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/viewmodel/NavigationViewModel.kt",
        ).readText()
        val commonStorageModule = root.resolve(
            "common/data/src/main/java/com/ssafy/jjongle/common/data/di/StorageDataModule.kt",
        ).readText()

        assertTrue(settings.contains("""include(":main:entity")"""))
        assertTrue(settings.contains("""include(":main:domain")"""))
        assertTrue(settings.contains("""include(":main:data")"""))
        assertTrue(settings.contains("""include(":main:presentation")"""))
        assertTrue(appBuild.contains("""implementation(project(":main:entity"))"""))
        assertTrue(appBuild.contains("""implementation(project(":main:data"))"""))
        assertTrue(appBuild.contains("""implementation(project(":main:presentation"))"""))

        assertTrue(mainEntityBuild.contains("alias(libs.plugins.kotlin.jvm)"))
        assertTrue(mainEntityBuild.contains("""api(project(":common:entity"))"""))
        assertTrue(mainEntityBuild.contains("""api(project(":tti"))"""))
        assertTrue(mainDomainBuild.contains("""api(project(":main:entity"))"""))
        assertTrue(mainDataBuild.contains("alias(libs.plugins.android.library)"))
        assertTrue(mainDataBuild.contains("""implementation(project(":main:domain"))"""))
        assertTrue(mainDataBuild.contains("""implementation(project(":common:data"))"""))

        val mapTtiPage = root.resolve(
            "main/entity/src/main/kotlin/com/ssafy/jjongle/main/entity/tti/MapTTIPage.kt",
        ).readText()
        assertTrue(mapTtiPage.contains("package com.ssafy.jjongle.main.entity.tti"))
        assertTrue(mapTtiPage.contains("object MapTTIPage"))
        assertFalse(
            "Main entity-owned TTI page must not remain in main domain",
            Files.exists(root.resolve("main/domain/src/main/kotlin/com/ssafy/jjongle/main/domain/tti/MapTTIPage.kt")),
        )

        assertTrue(navigationRepository.contains("package com.ssafy.jjongle.main.domain.repository"))
        assertTrue(navigationRepository.contains("interface NavigationStateRepository"))
        assertTrue(navigationRepositoryImpl.contains("package com.ssafy.jjongle.main.data.repository"))
        assertTrue(navigationRepositoryImpl.contains("import com.ssafy.jjongle.main.domain.repository.NavigationStateRepository"))
        assertTrue(mainDataModule.contains("class MainDataModule"))
        assertTrue(mainDataModule.contains("bindNavigationStateRepository"))
        assertTrue(navigationViewModel.contains("import com.ssafy.jjongle.main.domain.repository.NavigationStateRepository"))
        assertFalse(commonStorageModule.contains("bindNavigationStateRepository"))
        assertFalse(commonStorageModule.contains("NavigationStateRepositoryImpl"))
    }

    @Test
    fun presentation_modules_do_not_depend_on_data_modules() {
        val root = repositoryRoot()
        val presentationModules = listOf(
            "common/presentation",
            "main/presentation",
            "oxgame/presentation",
            "tangram/presentation",
        )

        presentationModules.forEach { module ->
            val build = root.resolve("$module/build.gradle.kts").readText()
            assertFalse(
                "$module must not depend on data modules directly; communicate through domain interfaces",
                Regex("""project\(":.*:data"\)""").containsMatchIn(build),
            )
            assertFalse(
                "$module must not depend on common:data directly; common data implementations stay below domain",
                build.contains("""project(":common:data")"""),
            )
        }

        val offenders = sourceFiles(root, presentationModules)
            .flatMap { file ->
                val source = file.readText()
                Regex("""import\s+com\.ssafy\.jjongle(?:\.\w+)*\.data\.""")
                    .findAll(source)
                    .map { match -> "${root.relativize(file)} imports ${match.value.trim()}" }
                    .toList()
            }

        assertTrue(
            "Presentation sources must not import data packages directly: $offenders",
            offenders.isEmpty(),
        )
    }

    @Test
    fun lower_layers_do_not_depend_on_presentation_or_app_layers() {
        val root = repositoryRoot()
        val lowerLayerModules = listOf(
            "common/entity",
            "common/domain",
            "common/data",
            "main/entity",
            "main/domain",
            "main/data",
            "oxgame/entity",
            "oxgame/domain",
            "oxgame/data",
            "tangram/entity",
            "tangram/domain",
            "tangram/data",
            "tti",
        )

        lowerLayerModules.forEach { module ->
            val build = root.resolve("$module/build.gradle.kts").readText()
            assertFalse(
                "$module must not depend on presentation modules",
                Regex("""project\(":.*:presentation"\)""").containsMatchIn(build),
            )
            assertFalse(
                "$module must not depend on :app",
                build.contains("""project(":app")"""),
            )
        }

        val forbiddenImport = Regex(
            """import\s+com\.ssafy\.jjongle(?:\.\w+)*\.(presentation|ui|viewmodel)\.""",
        )
        val offenders = sourceFiles(root, lowerLayerModules)
            .flatMap { file ->
                val source = file.readText()
                forbiddenImport.findAll(source)
                    .map { match -> "${root.relativize(file)} imports ${match.value.trim()}" }
                    .toList()
            }

        assertTrue(
            "Entity/domain/data/tti sources must not import presentation/app UI layers: $offenders",
            offenders.isEmpty(),
        )
    }

    @Test
    fun unsupported_explicit_backing_fields_flag_is_not_left_as_noop_build_noise() {
        val root = repositoryRoot()
        val rootBuild = root.resolve("build.gradle.kts").readText()
        assertFalse(rootBuild.contains("-Xexplicit-backing-fields"))
    }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }

    private fun sourceFiles(root: Path, modules: List<String>): List<Path> =
        modules
            .map(root::resolve)
            .filter(Files::exists)
            .flatMap { moduleRoot ->
                Files.walk(moduleRoot).use { paths ->
                    paths
                        .filter(Files::isRegularFile)
                        .filter { path -> path.toString().endsWith(".kt") }
                        .filter { path -> "/src/main/" in path.toString() }
                        .toList()
                }
            }
}
