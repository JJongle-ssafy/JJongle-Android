package com.ssafy.jjongle.data.architecture

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TangramDtoVoBoundaryContractTest {

    @Test
    fun tangram_remote_models_use_dto_names_and_to_vo_mapping_boundary() {
        val remoteModelSources = projectRoot()
            .resolve("tangram/data/src/main/java/com/ssafy/jjongle/tangram/data/remote/model")
            .walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .joinToString(separator = "\n") { it.readText() }
        val repository = source(
            "tangram/data/src/main/java/com/ssafy/jjongle/tangram/data/repository/TangramGameRepositoryImpl.kt"
        )
        val apiService = source(
            "tangram/data/src/main/java/com/ssafy/jjongle/tangram/data/remote/TangramGameApiService.kt"
        )

        assertTrue(remoteModelSources.contains("data class SingleGameDTO("))
        assertTrue(remoteModelSources.contains("data class TangramDetailDTO("))
        assertTrue(remoteModelSources.contains("data class TangramHistoriesPageDTO("))
        assertTrue(remoteModelSources.contains("data class TangramHistoryItemDTO("))
        assertTrue(remoteModelSources.contains("fun SingleGameDTO.toVO()"))
        assertTrue(remoteModelSources.contains("fun TangramDetailDTO.toVO("))
        assertTrue(remoteModelSources.contains("fun TangramHistoriesPageDTO.toVO()"))
        assertTrue(remoteModelSources.contains("fun TangramHistoryItemDTO.toVO()"))
        assertTrue(remoteModelSources.contains("""@SerialName("is_last")"""))
        assertTrue(remoteModelSources.contains("""@SerialName("tangram_id")"""))
        assertFalse(remoteModelSources.contains("Response"))
        assertFalse(remoteModelSources.contains("toDomain"))
        assertTrue(repository.contains(".toVO("))
        assertFalse(repository.contains(".stage ?:"))
        assertFalse(repository.contains("toDomain"))
        assertTrue(apiService.contains("Response<TangramHistoriesPageDTO>"))
        assertTrue(apiService.contains("Response<TangramDetailDTO>"))
        assertTrue(apiService.contains("Response<SingleGameDTO>"))
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
