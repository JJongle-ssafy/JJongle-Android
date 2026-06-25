package com.ssafy.jjongle.common.data.repository

import android.content.SharedPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Settings의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class SettingsRepositoryImplTest {

    @Test
    fun bgmEnabled_defaultsToTrueAndPersistsUpdates() = runBlocking {
        val preferences = InMemorySharedPreferences()
        val repository = SettingsRepositoryImpl(preferences)

        assertTrue(repository.getBgmEnabled().first())

        repository.setBgmEnabled(false)

        assertFalse(repository.getBgmEnabled().first())
        assertFalse(preferences.getBoolean("bgm_enabled", true))
    }

    private class InMemorySharedPreferences : SharedPreferences {
        private val values = mutableMapOf<String, Any?>()

        override fun getBoolean(key: String, defValue: Boolean): Boolean =
            values[key] as? Boolean ?: defValue

        override fun edit(): SharedPreferences.Editor = Editor()

        override fun getAll(): MutableMap<String, *> = values
        override fun getString(key: String?, defValue: String?): String? = values[key] as? String ?: defValue
        override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? =
            @Suppress("UNCHECKED_CAST")
            (values[key] as? MutableSet<String>) ?: defValues
        override fun getInt(key: String?, defValue: Int): Int = values[key] as? Int ?: defValue
        override fun getLong(key: String?, defValue: Long): Long = values[key] as? Long ?: defValue
        override fun getFloat(key: String?, defValue: Float): Float = values[key] as? Float ?: defValue
        override fun contains(key: String?): Boolean = values.containsKey(key)
        override fun registerOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener?
        ) = Unit
        override fun unregisterOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener?
        ) = Unit

        private inner class Editor : SharedPreferences.Editor {
            private val pending = mutableMapOf<String, Any?>()
            private var clearRequested = false

            override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor =
                apply { pending[key] = value }

            override fun apply() {
                commit()
            }

            override fun commit(): Boolean {
                if (clearRequested) values.clear()
                pending.forEach { (key, value) ->
                    if (value == null) values.remove(key) else values[key] = value
                }
                pending.clear()
                clearRequested = false
                return true
            }

            override fun putString(key: String?, value: String?): SharedPreferences.Editor =
                apply { key?.let { pending[it] = value } }
            override fun putStringSet(
                key: String?,
                values: MutableSet<String>?
            ): SharedPreferences.Editor = apply { key?.let { pending[it] = values } }
            override fun putInt(key: String?, value: Int): SharedPreferences.Editor =
                apply { key?.let { pending[it] = value } }
            override fun putLong(key: String?, value: Long): SharedPreferences.Editor =
                apply { key?.let { pending[it] = value } }
            override fun putFloat(key: String?, value: Float): SharedPreferences.Editor =
                apply { key?.let { pending[it] = value } }
            override fun remove(key: String?): SharedPreferences.Editor =
                apply { key?.let { pending[it] = null } }
            override fun clear(): SharedPreferences.Editor = apply { clearRequested = true }
        }
    }
}
