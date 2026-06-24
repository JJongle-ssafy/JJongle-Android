package com.ssafy.jjongle.data.architecture

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkSerializationContractTest {

    @Test
    fun common_network_module_uses_kotlinx_json_policy_instead_of_gson() {
        val networkModule = source(
            "common/data/src/main/java/com/ssafy/jjongle/common/data/di/NetworkModule.kt"
        )
        val commonDataGradle = source("common/data/build.gradle.kts")
        val tangramDataGradle = source("tangram/data/build.gradle.kts")
        val versionCatalog = source("gradle/libs.versions.toml")

        assertTrue(networkModule.contains("import kotlinx.serialization.json.Json"))
        assertTrue(networkModule.contains("fun provideJson(): Json"))
        assertTrue(networkModule.contains("ignoreUnknownKeys = true"))
        assertTrue(networkModule.contains("explicitNulls = false"))
        assertTrue(networkModule.contains("coerceInputValues = true"))
        assertTrue(networkModule.contains("asConverterFactory"))
        assertFalse(networkModule.contains("GsonConverterFactory"))
        assertFalse(networkModule.contains("GsonBuilder"))
        assertTrue(commonDataGradle.contains("libs.retrofit.converter.kotlinx.serialization"))
        assertFalse(commonDataGradle.contains("libs.retrofit.converter.gson"))
        assertTrue(tangramDataGradle.contains("libs.plugins.kotlin.serialization"))
        assertFalse(tangramDataGradle.contains("libs.retrofit.converter.gson"))
        assertTrue(versionCatalog.contains("kotlinx-serialization-json"))
        assertTrue(versionCatalog.contains("retrofit2-kotlinx-serialization-converter"))
        assertFalse(versionCatalog.contains("converter-gson"))
    }

    @Test
    fun tangram_remote_dtos_are_serializable() {
        val dtoSources = projectRoot()
            .resolve("tangram/data/src/main/java/com/ssafy/jjongle/tangram/data/remote/model")
            .walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .joinToString(separator = "\n") { it.readText() }

        assertTrue(dtoSources.contains("import kotlinx.serialization.Serializable"))
        assertTrue(dtoSources.contains("@Serializable\ndata class SingleGameDTO("))
        assertTrue(dtoSources.contains("@Serializable\ndata class TangramDetailDTO("))
        assertTrue(dtoSources.contains("@Serializable\ndata class TangramHistoriesPageDTO("))
        assertTrue(dtoSources.contains("@Serializable\ndata class TangramHistoryItemDTO("))
    }

    private fun source(path: String): String = projectRoot().resolve(path).readText()

    private fun projectRoot(): File {
        var current = File(".").absoluteFile
        while (current.parentFile != null) {
            if (current.resolve("settings.gradle.kts").exists()) return current
            current = current.parentFile!!
        }
        error("Project root not found")
    }
}
