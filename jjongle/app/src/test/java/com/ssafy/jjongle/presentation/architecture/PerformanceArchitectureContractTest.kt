package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PerformanceArchitectureContractTest {

    @Test
    fun tti_is_a_standalone_jvm_module_and_domain_uses_that_boundary() {
        val root = repositoryRoot()
        val settings = root.resolve("settings.gradle.kts").readText()
        val commonDomainBuild = root.resolve("common/domain/build.gradle.kts").readText()
        val ttiBuild = root.resolve("tti/build.gradle.kts").readText()
        val ttiHelper = root.resolve("tti/src/main/kotlin/com/ssafy/jjongle/tti/TTIHelper.kt").readText()
        val ttiHelperImpl = root.resolve("tti/src/main/kotlin/com/ssafy/jjongle/tti/TTIHelperImpl.kt").readText()
        val ttiPage = root.resolve("tti/src/main/kotlin/com/ssafy/jjongle/tti/TTIPage.kt").readText()
        val ttiEnums = root.resolve("tti/src/main/kotlin/com/ssafy/jjongle/tti/TTIEnums.kt").readText()
        val baseUseCase = root.resolve(
            "common/domain/src/main/kotlin/com/ssafy/jjongle/common/domain/base/BaseUseCase.kt",
        ).readText()
        val appModule = root.resolve("app/src/main/java/com/ssafy/jjongle/di/AppModule.kt").readText()
        val performanceLogger = root.resolve(
            "common/presentation/src/main/kotlin/com/ssafy/jjongle/common/presentation/diagnostics/PerformanceLogger.kt",
        ).readText()

        assertTrue(settings.contains("""include(":tti")"""))
        assertTrue(ttiBuild.contains("alias(libs.plugins.kotlin.jvm)"))
        assertFalse(ttiBuild.contains("android"))
        assertTrue(commonDomainBuild.contains("""api(project(":tti"))"""))
        assertFalse(
            Files.exists(
                root.resolve("common/domain/src/main/kotlin/com/ssafy/jjongle/common/domain/helper/TTIHelper.kt"),
            ),
        )

        assertTrue(ttiPage.contains("interface TTIPage"))
        assertTrue(ttiPage.contains("val pageName: String"))
        assertTrue(ttiEnums.contains("enum class TTITimelineCategory"))
        assertTrue(ttiEnums.contains("API_REQUEST_READY_TIME"))
        assertTrue(ttiEnums.contains("API_RESPONSE_TIME"))
        assertTrue(ttiEnums.contains("VIEW_CREATION"))
        assertTrue(ttiEnums.contains("VIEW_BINDING"))
        assertTrue(ttiEnums.contains("IMAGE_LOADED"))
        assertTrue(ttiEnums.contains("enum class TTIMetadata"))

        assertTrue(ttiHelper.contains("interface TTIHelper"))
        assertTrue(ttiHelper.contains("fun startTTITracking(page: TTIPage)"))
        assertTrue(ttiHelper.contains("fun startTTITimeline(page: TTIPage, timelineCategory: TTITimelineCategory)"))
        assertTrue(ttiHelper.contains("fun endTTITimeline(page: TTIPage, timelineCategory: TTITimelineCategory)"))
        assertTrue(ttiHelper.contains("fun endTTITracking(page: TTIPage)"))
        assertTrue(ttiHelper.contains("fun shotTTILogging(page: TTIPage)"))
        assertTrue(ttiHelper.contains("fun addTTIMetaData(page: TTIPage, metadata: TTIMetadata, value: String)"))
        assertTrue(ttiHelper.contains("object NoOp : TTIHelper"))

        assertTrue(ttiHelperImpl.contains("class TTIHelperImpl("))
        assertTrue(ttiHelperImpl.contains("TTI_TIMEOUT_MILLISECONDS"))
        assertTrue(ttiHelperImpl.contains("Shot TTI Logging"))
        assertTrue(ttiHelperImpl.contains("tti.tti_time"))
        assertTrue(ttiHelperImpl.contains("tti.view_binding_time"))
        assertTrue(ttiHelperImpl.contains("tti.is_bounced"))
        assertTrue(ttiHelperImpl.contains("tti.is_timeout"))

        assertTrue(baseUseCase.contains("import com.ssafy.jjongle.tti.TTIHelper"))
        assertFalse(baseUseCase.contains("common.domain.helper.TTIHelper"))
        assertTrue(appModule.contains("import com.ssafy.jjongle.tti.TTIHelper"))
        assertTrue(appModule.contains("import com.ssafy.jjongle.tti.TTIHelperImpl"))
        assertTrue(appModule.contains("import com.ssafy.jjongle.common.presentation.diagnostics.PerformanceLogger"))
        assertTrue(appModule.contains("fun provideTTIHelper(): TTIHelper = TTIHelperImpl("))
        assertTrue(appModule.contains("""PerformanceLogger.NoOp.log("TTI", message)"""))
        assertFalse(appModule.contains("fun provideTTIHelper(): TTIHelper = TTIHelper.NoOp"))
        assertFalse(appModule.contains("import android.util.Log"))
        assertFalse(appModule.contains("Log.d("))
        assertTrue(performanceLogger.contains("fun interface PerformanceLogger"))
        assertTrue(performanceLogger.contains("val NoOp = PerformanceLogger { _, _ -> }"))
    }

    @Test
    fun navigation_host_applies_page_level_jankstats_instrumentation() {
        val root = repositoryRoot()
        val versions = root.resolve("gradle/libs.versions.toml").readText()
        val commonPresentationBuild = root.resolve("common/presentation/build.gradle.kts").readText()
        val jankPageEffect = root.resolve(
            "common/presentation/src/main/kotlin/com/ssafy/jjongle/common/presentation/jank/JankPageEffect.kt",
        ).readText()
        val jankReporter = root.resolve(
            "common/presentation/src/main/kotlin/com/ssafy/jjongle/common/presentation/jank/JankReporter.kt",
        ).readText()
        val jankScrollWatcher = root.resolve(
            "common/presentation/src/main/kotlin/com/ssafy/jjongle/common/presentation/jank/JankScrollWatcher.kt",
        ).readText()
        val localJankReporter = root.resolve(
            "common/presentation/src/main/kotlin/com/ssafy/jjongle/common/presentation/jank/LocalJankReporter.kt",
        ).readText()
        val appModule = root.resolve("app/src/main/java/com/ssafy/jjongle/di/AppModule.kt").readText()
        val mainActivity = root.resolve("app/src/main/java/com/ssafy/jjongle/MainActivity.kt").readText()
        val navGraph = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/navigation/NavGraph.kt",
        ).readText()
        val animalBookScreen = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/AnimalBookScreen.kt",
        ).readText()
        val performanceLogger = root.resolve(
            "common/presentation/src/main/kotlin/com/ssafy/jjongle/common/presentation/diagnostics/PerformanceLogger.kt",
        ).readText()

        assertTrue(versions.contains("""metricsPerformance = "1.0.0""""))
        assertTrue(versions.contains("""androidx-metrics-performance"""))
        assertTrue(commonPresentationBuild.contains("implementation(libs.androidx.metrics.performance)"))

        assertTrue(jankPageEffect.contains("import androidx.metrics.performance.JankStats"))
        assertTrue(jankPageEffect.contains("import androidx.metrics.performance.PerformanceMetricsState"))
        assertTrue(jankPageEffect.contains("fun JankPageEffect("))
        assertTrue(jankPageEffect.contains("reporter: JankReporter? = null"))
        assertTrue(jankPageEffect.contains("val activeReporter = reporter ?: LocalJankReporter.current"))
        assertTrue(jankPageEffect.contains("JankStats.createAndTrack(activity.window)"))
        assertTrue(jankPageEffect.contains("""metricsStateHolder.state?.putState("Page", pageName)"""))
        assertTrue(jankPageEffect.contains("""metricsStateHolder.state?.removeState("Page")"""))
        assertTrue(jankPageEffect.contains("Lifecycle.Event.ON_RESUME"))
        assertTrue(jankPageEffect.contains("Lifecycle.Event.ON_PAUSE"))
        assertTrue(jankPageEffect.contains("JankReportReason.PAGE_EXIT"))

        assertTrue(jankReporter.contains("interface JankReporter"))
        assertTrue(jankReporter.contains("class LoggingJankReporter("))
        assertTrue(jankReporter.contains("import com.ssafy.jjongle.common.presentation.diagnostics.PerformanceLogger"))
        assertTrue(jankReporter.contains("""PerformanceLogger.NoOp.log("JankStats", message)"""))
        assertFalse(jankReporter.contains("import android.util.Log"))
        assertFalse(jankReporter.contains("Log.d("))
        assertTrue(jankReporter.contains("enum class JankReportReason"))
        assertTrue(jankReporter.contains("PAGE_EXIT"))
        assertTrue(jankReporter.contains("SCROLL_END"))
        assertTrue(jankReporter.contains("FROZEN_FRAME"))
        assertTrue(jankReporter.contains("THRESHOLD_EXCEEDED"))
        assertTrue(jankReporter.contains("FROZEN_FRAME_THRESHOLD_NANOS"))
        assertTrue(jankReporter.contains("THRESHOLD_SAMPLE_FRAMES"))
        assertTrue(jankReporter.contains("JANK_RATIO_THRESHOLD"))
        assertTrue(jankReporter.contains("flush(JankReportReason.FROZEN_FRAME, frameData.pageName)"))
        assertTrue(jankReporter.contains("flush(JankReportReason.THRESHOLD_EXCEEDED, frameData.pageName)"))

        assertTrue(jankScrollWatcher.contains("fun JankScrollWatcher("))
        assertTrue(jankScrollWatcher.contains("scrollableState: ScrollableState"))
        assertTrue(jankScrollWatcher.contains("reporter: JankReporter? = null"))
        assertTrue(jankScrollWatcher.contains("val activeReporter = reporter ?: LocalJankReporter.current"))
        assertTrue(jankScrollWatcher.contains("snapshotFlow { scrollableState.isScrollInProgress }"))
        assertTrue(jankScrollWatcher.contains("distinctUntilChanged()"))
        assertTrue(jankScrollWatcher.contains("JankReportReason.SCROLL_END"))
        assertTrue(jankScrollWatcher.contains("activeReporter.flush(JankReportReason.SCROLL_END, pageName)"))
        assertTrue(localJankReporter.contains("staticCompositionLocalOf<JankReporter>"))
        assertTrue(localJankReporter.contains("DebugJankReporter"))
        assertTrue(appModule.contains("fun provideJankReporter(): JankReporter = LoggingJankReporter("))
        assertTrue(appModule.contains("""PerformanceLogger.NoOp.log("JankStats", message)"""))
        assertTrue(mainActivity.contains("lateinit var jankReporter: JankReporter"))
        assertTrue(mainActivity.contains("LocalJankReporter provides jankReporter"))

        assertTrue(navGraph.contains("import com.ssafy.jjongle.common.presentation.jank.JankPageEffect"))
        assertTrue(navGraph.contains("JankPageEffect(pageName = key.path)"))
        assertTrue(navGraph.contains("route.render(key.args, navigator)"))
        assertTrue(navGraph.indexOf("JankPageEffect(pageName = key.path)") < navGraph.indexOf("route.render(key.args, navigator)"))
        assertTrue(performanceLogger.contains("fun interface PerformanceLogger"))

        assertTrue(animalBookScreen.contains("import com.ssafy.jjongle.common.presentation.jank.JankScrollWatcher"))
        assertTrue(animalBookScreen.contains("""JankScrollWatcher(pageName = "animal_book_story", scrollableState = scroll)"""))
    }

    @Test
    fun map_screen_marks_tti_from_domain_page_and_presentation_lifecycle() {
        val root = repositoryRoot()
        val mapTtiPage = root.resolve(
            "main/entity/src/main/kotlin/com/ssafy/jjongle/main/entity/tti/MapTTIPage.kt",
        ).readText()
        val mapViewModel = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/viewmodel/MapViewModel.kt",
        ).readText()
        val mapScreen = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/MapScreen.kt",
        ).readText()
        val mapViewModelTest = root.resolve(
            "app/src/test/java/com/ssafy/jjongle/presentation/viewmodel/MapViewModelTest.kt",
        ).readText()

        assertTrue(mapTtiPage.contains("object MapTTIPage : TTIPage"))
        assertTrue(mapTtiPage.contains("""override val pageName: String = "map""""))

        assertTrue(mapViewModel.contains("import com.ssafy.jjongle.main.entity.tti.MapTTIPage"))
        assertTrue(mapViewModel.contains("private val ttiHelper: TTIHelper"))
        assertTrue(mapViewModel.contains("ttiHelper.startTTITracking(MapTTIPage)"))
        assertTrue(mapViewModel.contains("ttiHelper.startTTITimeline(MapTTIPage, TTITimelineCategory.VIEW_BINDING)"))
        assertTrue(mapViewModel.contains("fun onInitialContentDisplayed()"))
        assertTrue(mapViewModel.contains("ttiHelper.endTTITimeline(MapTTIPage, TTITimelineCategory.VIEW_BINDING)"))
        assertTrue(mapViewModel.contains("ttiHelper.endTTITracking(MapTTIPage)"))
        assertTrue(mapViewModel.contains("fun onPageLeaving()"))
        assertTrue(mapViewModel.contains("ttiHelper.shotTTILogging(MapTTIPage)"))

        assertTrue(mapScreen.contains("LaunchedEffect(Unit)"))
        assertTrue(mapScreen.contains("viewModel.onInitialContentDisplayed()"))
        assertTrue(mapScreen.contains("DisposableEffect(viewModel)"))
        assertTrue(mapScreen.contains("onDispose { viewModel.onPageLeaving() }"))

        assertTrue(mapViewModelTest.contains("fun map_view_model_marks_tti_lifecycle()"))
        assertTrue(mapViewModelTest.contains("RecordingTTIHelper"))
        assertTrue(mapViewModelTest.contains("start:map"))
        assertTrue(mapViewModelTest.contains("end:map"))
        assertTrue(mapViewModelTest.contains("shot:map"))
    }

    @Test
    fun baseline_profile_module_generates_and_measures_startup_hot_path() {
        val root = repositoryRoot()
        val settings = root.resolve("settings.gradle.kts").readText()
        val versions = root.resolve("gradle/libs.versions.toml").readText()
        val appBuild = root.resolve("app/build.gradle.kts").readText()
        val baselineBuild = root.resolve("baselineprofile/build.gradle.kts").readText()
        val navGraph = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/navigation/NavGraph.kt",
        ).readText()
        val mapScreen = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/MapScreen.kt",
        ).readText()
        val mypageScreen = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/MypageScreen.kt",
        ).readText()
        val animalBookScreen = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/AnimalBookScreen.kt",
        ).readText()
        val generator = root.resolve(
            "baselineprofile/src/main/java/com/ssafy/jjongle/baselineprofile/BaselineProfileGenerator.kt",
        ).readText()
        val benchmark = root.resolve(
            "baselineprofile/src/main/java/com/ssafy/jjongle/baselineprofile/StartupBenchmark.kt",
        ).readText()

        assertTrue(settings.contains("""include(":baselineprofile")"""))
        assertTrue(versions.contains("""benchmark = "1.4.1""""))
        assertTrue(versions.contains("""androidx-baselineprofile"""))
        assertTrue(versions.contains("""androidx-benchmark-macro-junit4"""))
        assertTrue(versions.contains("""androidx-uiautomator"""))

        assertTrue(appBuild.contains("alias(libs.plugins.androidx.baselineprofile)"))
        assertTrue(appBuild.contains("""create("benchmark")"""))
        assertTrue(appBuild.contains("initWith(getByName(\"release\"))"))
        assertTrue(appBuild.contains("isProfileable = true"))
        assertTrue(appBuild.contains("automaticGenerationDuringBuild = false"))
        assertTrue(appBuild.contains("""baselineProfile(project(":baselineprofile"))"""))

        assertTrue(baselineBuild.contains("alias(libs.plugins.android.test)"))
        assertTrue(baselineBuild.contains("alias(libs.plugins.androidx.baselineprofile)"))
        assertTrue(baselineBuild.contains("""targetProjectPath = ":app""""))
        assertTrue(baselineBuild.contains("minSdk = 28"))
        assertTrue(baselineBuild.contains("sourceCompatibility = JavaVersion.VERSION_17"))
        assertTrue(baselineBuild.contains("targetCompatibility = JavaVersion.VERSION_17"))
        assertTrue(baselineBuild.contains("""jvmTarget = "17""""))
        assertTrue(baselineBuild.contains("implementation(libs.androidx.benchmark.macro.junit4)"))
        assertTrue(baselineBuild.contains("implementation(libs.androidx.uiautomator)"))
        assertTrue(baselineBuild.contains("useConnectedDevices = true"))

        assertTrue(navGraph.contains("testTagsAsResourceId = true"))
        assertTrue(mapScreen.contains("""testTag("map_mypage_panel")"""))
        assertTrue(mypageScreen.contains("""testTag("mypage_animal_book_button")"""))
        assertTrue(animalBookScreen.contains("""testTag("animal_book_item_${'$'}{slot.id}")"""))
        assertTrue(animalBookScreen.contains("""testTag("animal_detail_camera_button")"""))

        assertTrue(generator.contains("BaselineProfileRule"))
        assertTrue(generator.contains("baselineProfileRule.collect("))
        assertTrue(generator.contains("""packageName = PACKAGE_NAME"""))
        assertTrue(generator.contains("startActivityAndWait()"))
        assertTrue(generator.contains("""waitAndClick("map_mypage_panel")"""))
        assertTrue(generator.contains("""waitAndClick("mypage_animal_book_button")"""))
        assertTrue(generator.contains("""By.res(PACKAGE_NAME, "animal_book_item_BEAR")"""))
        assertTrue(generator.contains("""By.res(PACKAGE_NAME, "animal_detail_camera_button")"""))

        assertTrue(benchmark.contains("MacrobenchmarkRule"))
        assertTrue(benchmark.contains("StartupTimingMetric()"))
        assertTrue(benchmark.contains("StartupMode.COLD"))
        assertTrue(benchmark.contains("CompilationMode.Partial()"))
        assertTrue(benchmark.contains("measureRepeated("))
        assertTrue(benchmark.contains("iterations = 5"))
    }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
}
