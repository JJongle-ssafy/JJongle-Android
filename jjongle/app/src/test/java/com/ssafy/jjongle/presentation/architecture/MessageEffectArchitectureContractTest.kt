package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MessageEffectArchitectureContractTest {

    @Test
    fun app_module_provides_message_helper_without_android_toast_bridge() {
        val source = sourcePath("app/src/main/java/com/ssafy/jjongle/di/AppModule.kt").readText()

        assertFalse(
            "AppModule must not implement MessageHelper with Android Toast",
            source.contains("android.widget.Toast") || source.contains("Toast.makeText"),
        )
        assertTrue(
            "AppModule must provide the presentation MessageEffectBus singleton",
            source.contains("fun provideMessageEffectBus(): MessageEffectBus = MessageEffectBus()"),
        )
        assertTrue(
            "MessageHelper must be backed by the MessageEffectBus",
            source.contains("fun provideMessageHelper(messageEffectBus: MessageEffectBus): MessageHelper = messageEffectBus"),
        )
    }

    @Test
    fun main_activity_subscribes_message_effects_at_root() {
        val source = sourcePath("app/src/main/java/com/ssafy/jjongle/MainActivity.kt").readText()

        assertTrue(
            "MainActivity must inject MessageEffectBus",
            source.contains("lateinit var messageEffectBus: MessageEffectBus"),
        )
        assertTrue(
            "MainActivity root must host snackbar/dialog effects",
            source.contains("MessageEffectHost(") &&
                source.contains("messageEffectBus = messageEffectBus"),
        )
    }

    @Test
    fun message_effect_bus_maps_domain_helper_calls_to_presentation_effects() {
        val source = sourcePath(
            "common/presentation/src/main/kotlin/com/ssafy/jjongle/common/presentation/message/MessageEffectBus.kt"
        ).readText()

        assertTrue(
            "MessageEffectBus must implement the domain MessageHelper boundary",
            source.contains("class MessageEffectBus : MessageHelper"),
        )
        assertTrue(
            "MessageEffectBus must expose a SharedFlow for root presentation collection",
            source.contains("val effects: SharedFlow<MessageEffect>"),
        )
        assertTrue(
            "Legacy toast requests must map to snackbar effects instead of Android Toast",
            source.contains("override fun showToast(messageText: String)") &&
                source.contains("showSnackBar(messageText)"),
        )
    }

    private fun sourcePath(relativePath: String): Path {
        val root = generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
        return root.resolve(relativePath)
    }
}
