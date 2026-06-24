package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.streams.asSequence
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DesignTokenArchitectureContractTest {

    @Test
    fun common_presentation_declares_design_token_layers() {
        val root = repositoryRoot()
        val tokenRoot = root.resolve(
            "common/presentation/src/main/kotlin/com/ssafy/jjongle/common/presentation/ui",
        )

        val designTokens = tokenRoot.resolve("token/DesignTokens.kt").readText()
        val colorSemantic = tokenRoot.resolve("color/ColorSemantic.kt").readText()
        val typeScale = tokenRoot.resolve("typo/ArchiTypeScale.kt").readText()
        val archiTheme = tokenRoot.resolve("theme/ArchiTheme.kt").readText()

        assertTrue(designTokens.contains("object ArchiPaletteColors"))
        assertTrue(designTokens.contains("val DefaultArchiColor = ArchiSemanticColors"))
        assertTrue(designTokens.contains("val DefaultArchiStaticTypeScale = ArchiTypeScale"))
        assertTrue(designTokens.contains("FIGMA-TOKEN-INJECTION-POINT: palette"))
        assertTrue(designTokens.contains("FIGMA-TOKEN-INJECTION-POINT: semantic-colors"))
        assertTrue(designTokens.contains("FIGMA-TOKEN-INJECTION-POINT: type-scale"))

        assertTrue(colorSemantic.contains("data class ArchiSemanticColors"))
        assertTrue(colorSemantic.contains("fun withStringKey"))
        assertTrue(typeScale.contains("data class ArchiTypeScale"))
        assertTrue(typeScale.contains("fun withStringKey"))
        assertTrue(archiTheme.contains("object ArchiThemeImpl"))
        assertTrue(archiTheme.contains("val archiColor: ArchiSemanticColors"))
        assertTrue(archiTheme.contains("val typeScale: ArchiTypeScale"))
        assertTrue(archiTheme.contains("fun ArchiTheme("))
    }

    @Test
    fun app_theme_injects_archi_theme_and_common_button_consumes_tokens() {
        val root = repositoryRoot()
        val appTheme = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/theme/Theme.kt",
        ).readText()
        val baseButton = root.resolve(
            "common/presentation/src/main/kotlin/com/ssafy/jjongle/common/presentation/ui/component/BaseButton.kt",
        ).readText()
        val archiText = root.resolve(
            "common/presentation/src/main/kotlin/com/ssafy/jjongle/common/presentation/ui/component/Text.kt",
        ).readText()
        val splashScreen = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/SplashScreen.kt",
        ).readText()
        val mapScreen = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/MapScreen.kt",
        ).readText()
        val introScreen = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/IntroScreen.kt",
        ).readText()
        val loginScreen = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/LoginScreen.kt",
        ).readText()
        val cameraScreen = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/CameraScreen.kt",
        ).readText()
        val settingScreen = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/SettingScreen.kt",
        ).readText()
        val mypageScreen = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/MypageScreen.kt",
        ).readText()
        val quizNoteScreen = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/QuizNoteScreen.kt",
        ).readText()
        val animalBookScreen = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/AnimalBookScreen.kt",
        ).readText()
        val profileDialog = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/component/ProfileDialog.kt",
        ).readText()
        val legacyType = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/theme/Type.kt",
        ).readText()
        val responsiveLayout = root.resolve(
            "common/presentation/src/main/kotlin/com/ssafy/jjongle/common/presentation/ui/layout/ResponsiveLayout.kt",
        ).readText()
        val tangramTitleScreen = root.resolve(
            "tangram/presentation/src/main/java/com/ssafy/jjongle/tangram/presentation/ui/screen/TangramTitleScreen.kt",
        ).readText()
        val tangramTutorialScreen = root.resolve(
            "tangram/presentation/src/main/java/com/ssafy/jjongle/tangram/presentation/ui/screen/TangramTutorialScreen.kt",
        ).readText()
        val tangramStageScreen = root.resolve(
            "tangram/presentation/src/main/java/com/ssafy/jjongle/tangram/presentation/ui/screen/TangramStageScreen.kt",
        ).readText()
        val oxGameUiElements = root.resolve(
            "oxgame/presentation/src/main/java/com/ssafy/jjongle/oxgame/presentation/ui/screen/OXGameUiElements.kt",
        ).readText()
        val oxGameTitleScreen = root.resolve(
            "oxgame/presentation/src/main/java/com/ssafy/jjongle/oxgame/presentation/ui/screen/OXGameTitleScreen.kt",
        ).readText()
        val oxGameScreen = root.resolve(
            "oxgame/presentation/src/main/java/com/ssafy/jjongle/oxgame/presentation/ui/screen/OXGameScreen.kt",
        ).readText()
        val oxCameraComponent = root.resolve(
            "oxgame/presentation/src/main/java/com/ssafy/jjongle/oxgame/presentation/ui/component/CameraComponent.kt",
        ).readText()
        val mainActivity = root.resolve("app/src/main/java/com/ssafy/jjongle/MainActivity.kt").readText()

        assertTrue(appTheme.contains("ArchiTheme {"))
        assertTrue(mainActivity.contains("import com.ssafy.jjongle.common.presentation.ui.theme.ArchiThemeImpl"))
        assertTrue(mainActivity.contains("color = ArchiThemeImpl.archiColor.bgDefaultLevel0"))
        assertFalse(mainActivity.contains("MaterialTheme.colorScheme"))
        assertFalse(mainActivity.contains("import androidx.compose.material3.MaterialTheme"))
        assertTrue(baseButton.contains("ArchiThemeImpl.archiColor"))
        assertTrue(baseButton.contains("ArchiText("))
        assertTrue(baseButton.contains("textStyle: TextStyle = ArchiThemeImpl.typeScale.titleStrongM"))
        assertFalse(baseButton.contains("Color(0x"))
        assertFalse(baseButton.contains("30.sp"))
        assertTrue(archiText.contains("style: TextStyle = ArchiThemeImpl.typeScale.textRegularM"))
        assertTrue(archiText.contains("color: Color = ArchiThemeImpl.archiColor.contentDefaultLevel0"))
        assertTrue(archiText.contains("textAlign: TextAlign? = null"))
        assertTrue(archiText.contains("softWrap: Boolean = true"))
        assertTrue(archiText.contains("maxLines: Int = Int.MAX_VALUE"))
        assertTrue(splashScreen.contains("ArchiText("))
        assertTrue(splashScreen.contains("ArchiThemeImpl.typeScale.titleStrongM"))
        assertTrue(splashScreen.contains("ArchiThemeImpl.archiColor.contentOnBrand"))
        assertFalse(splashScreen.contains("androidx.compose.material3.Text"))
        assertFalse(splashScreen.contains("Color(0x"))
        assertFalse(splashScreen.contains(".sp"))
        assertTrue(mapScreen.contains("ArchiText("))
        assertTrue(mapScreen.contains("ArchiThemeImpl.typeScale.textStrongL"))
        assertTrue(mapScreen.contains("ArchiThemeImpl.archiColor.contentOnBrand"))
        assertFalse(mapScreen.contains("androidx.compose.material3.Text"))
        assertFalse(mapScreen.contains("Color(0x"))
        assertFalse(mapScreen.contains(".sp"))
        assertTrue(introScreen.contains("ArchiText("))
        assertTrue(introScreen.contains("ArchiThemeImpl.archiColor"))
        assertTrue(introScreen.contains("ArchiThemeImpl.typeScale"))
        assertFalse(introScreen.contains("androidx.compose.material3.Text"))
        assertFalse(introScreen.contains("Color(0x"))
        assertFalse(Regex("\\d+\\.sp").containsMatchIn(introScreen))
        assertTrue(loginScreen.contains("ArchiText("))
        assertTrue(loginScreen.contains("ArchiThemeImpl.archiColor"))
        assertTrue(loginScreen.contains("ArchiThemeImpl.typeScale"))
        assertFalse(loginScreen.contains("androidx.compose.material3.Text"))
        assertFalse(loginScreen.contains("Color.White"))
        assertFalse(loginScreen.contains("Color.LightGray"))
        assertFalse(loginScreen.contains("Color.Red"))
        assertTrue(cameraScreen.contains("ArchiThemeImpl.archiColor"))
        assertFalse(cameraScreen.contains("Color(0x"))
        assertFalse(cameraScreen.contains("Color.White"))
        assertFalse(cameraScreen.contains("Color.Black"))
        assertFalse(cameraScreen.contains("Color.Red"))
        assertFalse(cameraScreen.contains("Color.Yellow"))
        assertTrue(settingScreen.contains("ArchiText("))
        assertTrue(settingScreen.contains("ArchiThemeImpl.typeScale.textStrongM"))
        assertTrue(settingScreen.contains("ArchiThemeImpl.typeScale.titleStrongM"))
        assertFalse(settingScreen.contains("androidx.compose.material3.Text("))
        assertFalse(settingScreen.contains("Color(0x"))
        assertFalse(settingScreen.contains(".sp"))
        assertTrue(mypageScreen.contains("ArchiText("))
        assertTrue(mypageScreen.contains("ArchiThemeImpl.archiColor"))
        assertTrue(mypageScreen.contains("ArchiThemeImpl.typeScale"))
        assertFalse(mypageScreen.contains("androidx.compose.material3.Text"))
        assertFalse(mypageScreen.contains("Color(0x"))
        assertFalse(mypageScreen.contains(".sp"))
        assertTrue(quizNoteScreen.contains("ArchiText("))
        assertTrue(quizNoteScreen.contains("ArchiThemeImpl.archiColor"))
        assertTrue(quizNoteScreen.contains("ArchiThemeImpl.typeScale"))
        assertFalse(quizNoteScreen.contains("androidx.compose.material3.Text"))
        assertFalse(quizNoteScreen.contains("Color(0x"))
        assertFalse(Regex("\\d+\\.sp").containsMatchIn(quizNoteScreen))
        assertTrue(animalBookScreen.contains("ArchiText("))
        assertTrue(animalBookScreen.contains("ArchiThemeImpl.archiColor"))
        assertTrue(animalBookScreen.contains("ArchiThemeImpl.typeScale"))
        assertFalse(animalBookScreen.contains("androidx.compose.material3.Text"))
        assertFalse(animalBookScreen.contains("Color(0x"))
        assertFalse(Regex("\\d+\\.sp").containsMatchIn(animalBookScreen))
        assertTrue(profileDialog.contains("ArchiText("))
        assertTrue(profileDialog.contains("ArchiThemeImpl.archiColor"))
        assertTrue(profileDialog.contains("ArchiThemeImpl.typeScale"))
        assertFalse(profileDialog.contains("androidx.compose.material3.Text("))
        assertFalse(profileDialog.contains("Color(0x"))
        assertFalse(Regex("\\d+\\.sp").containsMatchIn(profileDialog))
        assertTrue(legacyType.contains("DefaultArchiStaticTypeScale"))
        assertFalse(Regex("\\d+\\.sp").containsMatchIn(legacyType))
        assertTrue(responsiveLayout.contains("ArchiThemeImpl.archiColor.bgBrandLevel0"))
        assertFalse(responsiveLayout.contains("letterboxColor: Color = Color.Black"))
        assertTrue(tangramTitleScreen.contains("ArchiThemeImpl.typeScale.textStrongL"))
        assertTrue(tangramTitleScreen.contains("ArchiThemeImpl.typeScale.textStrongM"))
        assertFalse(tangramTitleScreen.contains(".sp"))
        assertTrue(tangramTutorialScreen.contains("ArchiThemeImpl.typeScale.textStrongM"))
        assertFalse(tangramTutorialScreen.contains(".sp"))
        assertTrue(tangramStageScreen.contains("ArchiThemeImpl.typeScale.textStrongM"))
        assertFalse(tangramStageScreen.contains(".sp"))
        assertTrue(oxGameUiElements.contains("ArchiThemeImpl.archiColor"))
        assertTrue(oxGameUiElements.contains("ArchiText("))
        assertTrue(oxGameUiElements.contains("textStyle: TextStyle = ArchiThemeImpl.typeScale.titleStrongM"))
        assertFalse(oxGameUiElements.contains("Color(0x"))
        assertFalse(oxGameUiElements.contains("androidx.compose.material3.Text"))
        assertFalse(oxGameUiElements.contains("30.sp"))
        assertTrue(oxGameTitleScreen.contains("ArchiThemeImpl.typeScale.textStrongL"))
        assertTrue(oxGameTitleScreen.contains("ArchiThemeImpl.typeScale.textStrongM"))
        assertFalse(oxGameTitleScreen.contains(".sp"))
        assertTrue(oxGameScreen.contains("ArchiText("))
        assertTrue(oxGameScreen.contains("ArchiThemeImpl.archiColor"))
        assertTrue(oxGameScreen.contains("textStyle: TextStyle = ArchiThemeImpl.typeScale.titleStrongM"))
        assertFalse(oxGameScreen.contains("import androidx.compose.material3.Text\n"))
        assertFalse(oxGameScreen.contains("MaterialTheme"))
        assertFalse(oxGameScreen.contains("Color(0x"))
        assertFalse(oxGameScreen.contains("Color.White"))
        assertFalse(oxGameScreen.contains("Color.Black"))
        assertFalse(oxGameScreen.contains("Color.Red"))
        assertFalse(oxGameScreen.contains("Color.Yellow"))
        assertFalse(oxGameScreen.contains("Color.Transparent"))
        assertFalse(Regex("\\d+\\.sp").containsMatchIn(oxGameScreen))
        assertFalse(oxGameScreen.contains("fontSize: TextUnit = 30.sp"))
        assertFalse(oxGameScreen.contains("fontSize = 36.sp"))
        assertFalse(oxGameScreen.contains("fontSize = 30.sp"))
        assertFalse(oxGameScreen.contains("containerColor = Color(0xFF6F4B2A)"))
        assertFalse(oxGameScreen.contains("colors = CardDefaults.cardColors(containerColor = Color.White)"))
        assertFalse(oxGameScreen.contains("colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))"))
        assertFalse(oxGameScreen.contains("Color(0xFF8B4513)"))
        assertFalse(oxGameScreen.contains("Color(0xFFFFD700)"))
        assertTrue(oxCameraComponent.contains("ArchiText("))
        assertTrue(oxCameraComponent.contains("ArchiThemeImpl.archiColor"))
        assertFalse(oxCameraComponent.contains("MaterialTheme"))
        assertFalse(oxCameraComponent.contains("import androidx.compose.material3.Text\n"))
        assertFalse(oxCameraComponent.contains("Color.Black"))
        assertFalse(oxCameraComponent.contains("Color.White"))
    }

    @Test
    fun presentation_sources_do_not_introduce_raw_design_tokens_outside_token_boundaries() {
        val root = repositoryRoot()
        val sourceRoots = listOf(
            "app/src/main/java",
            "common/presentation/src/main/kotlin",
            "main/presentation/src/main/java",
            "oxgame/presentation/src/main/java",
            "tangram/presentation/src/main/java",
        ).map(root::resolve)

        val allowedRawTokenFiles = setOf(
            "common/presentation/src/main/kotlin/com/ssafy/jjongle/common/presentation/ui/token/DesignTokens.kt",
            "common/presentation/src/main/kotlin/com/ssafy/jjongle/common/presentation/ui/component/Text.kt",
            "common/presentation/src/main/kotlin/com/ssafy/jjongle/common/presentation/ui/theme/ArchiTheme.kt",
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/theme/Theme.kt",
        )
        val allowedMaterialThemeFiles = setOf(
            "common/presentation/src/main/kotlin/com/ssafy/jjongle/common/presentation/ui/theme/ArchiTheme.kt",
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/theme/Theme.kt",
        )
        val allowedMaterialTextFiles = setOf(
            "common/presentation/src/main/kotlin/com/ssafy/jjongle/common/presentation/ui/component/Text.kt",
        )

        val violations = mutableListOf<String>()
        sourceRoots
            .asSequence()
            .filter(Files::exists)
            .flatMap { sourceRoot ->
                Files.walk(sourceRoot).use { stream ->
                    stream.asSequence()
                        .filter { Files.isRegularFile(it) }
                        .filter { it.toString().endsWith(".kt") }
                        .toList()
                        .asSequence()
                }
            }
            .forEach { file ->
                val relativePath = root.relativize(file).toString()
                val source = file.readText()

                if (relativePath !in allowedRawTokenFiles) {
                    val rawPatterns = listOf(
                        "Color(0x",
                        "Color.White",
                        "Color.Black",
                        "Color.Red",
                        "Color.Yellow",
                        "Color.Transparent",
                        "Color.LightGray",
                    )
                    rawPatterns
                        .filter(source::contains)
                        .forEach { violations += "$relativePath contains raw color token `$it`" }

                    if (Regex("\\d+\\.sp").containsMatchIn(source)) {
                        violations += "$relativePath contains raw typography `.sp` token"
                    }
                }

                if (relativePath !in allowedMaterialThemeFiles && source.contains("MaterialTheme")) {
                    violations += "$relativePath consumes MaterialTheme directly"
                }

                if (
                    relativePath !in allowedMaterialTextFiles &&
                    source.contains("import androidx.compose.material3.Text\n")
                ) {
                    violations += "$relativePath imports Material Text directly"
                }
                if (
                    relativePath !in allowedMaterialTextFiles &&
                    source.contains("androidx.compose.material3.Text(")
                ) {
                    violations += "$relativePath calls fully-qualified Material Text directly"
                }
                if (
                    relativePath !in allowedMaterialTextFiles &&
                    (
                        source.contains("import androidx.compose.foundation.text.BasicText") ||
                            source.contains("BasicText(") ||
                            source.contains("androidx.compose.foundation.text.BasicText(")
                        )
                ) {
                    violations += "$relativePath uses BasicText directly instead of ArchiText"
                }
            }

        assertTrue(violations.joinToString(separator = "\n"), violations.isEmpty())
    }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
}
