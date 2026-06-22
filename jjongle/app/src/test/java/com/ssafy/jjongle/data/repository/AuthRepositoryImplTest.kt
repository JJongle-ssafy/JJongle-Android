package com.ssafy.jjongle.data.repository

import android.content.SharedPreferences
import com.ssafy.jjongle.data.firebase.FirebaseAuthenticatedUser
import com.ssafy.jjongle.data.firebase.FirebaseAuthDataSource
import com.ssafy.jjongle.data.firebase.UserProfileDataSource
import com.ssafy.jjongle.data.firebase.model.UserProfileDto
import com.ssafy.jjongle.data.local.AuthDataSource
import com.ssafy.jjongle.domain.entity.AuthException
import com.ssafy.jjongle.domain.entity.UserAlreadyExistsAuthError
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryImplTest {

    @Test
    fun login_returnsUnauthenticated_whenFirestoreProfileIsMissing() = runTest {
        val repository = createRepository()

        val result = repository.login("firebase-token")

        assertTrue(result.isSuccess)
        assertFalse(result.getOrThrow().isAuthenticated)
    }

    @Test
    fun login_restoresFirestoreProfileAndCachesLocalProfile() = runTest {
        val authDataSource = AuthDataSource(InMemorySharedPreferences())
        val userProfileDataSource = FakeUserProfileDataSource(
            profiles = mutableMapOf(
                "uid-1" to UserProfileDto(
                    nickname = "몽이",
                    profileImage = "MONGI"
                )
            )
        )
        val repository = createRepository(
            authDataSource = authDataSource,
            userProfileDataSource = userProfileDataSource
        )

        val result = repository.login("firebase-token")

        val state = result.getOrThrow()
        assertTrue(state.isAuthenticated)
        assertNull(state.accessToken)
        assertNull(state.refreshToken)
        assertEquals("몽이", state.user?.nickname)
        assertEquals("child@example.com", state.user?.email)
        assertEquals("MONGI", state.user?.profileImage)
        assertEquals("uid-1", authDataSource.getUserId())
        assertEquals("몽이", authDataSource.getNickname())
        assertEquals("MONGI", authDataSource.getProfileImage())
    }

    @Test
    fun signup_createsFirestoreProfileAndAuthenticates() = runTest {
        val userProfileDataSource = FakeUserProfileDataSource()
        val repository = createRepository(userProfileDataSource = userProfileDataSource)

        val result = repository.signup(
            idToken = "firebase-token",
            nickname = "토비",
            profileImage = "TOBY"
        )

        val state = result.getOrThrow()
        assertTrue(state.isAuthenticated)
        assertEquals("토비", state.user?.nickname)
        assertEquals("TOBY", state.user?.profileImage)
        assertEquals(
            UserProfileDto(
                nickname = "토비",
                profileImage = "TOBY",
                email = "child@example.com"
            ),
            userProfileDataSource.profiles["uid-1"]
        )
    }

    @Test
    fun signup_fails_whenFirestoreProfileAlreadyExists() = runTest {
        val userProfileDataSource = FakeUserProfileDataSource(
            profiles = mutableMapOf(
                "uid-1" to UserProfileDto(
                    nickname = "몽이",
                    profileImage = "MONGI"
                )
            )
        )
        val repository = createRepository(userProfileDataSource = userProfileDataSource)

        val result = repository.signup(
            idToken = "firebase-token",
            nickname = "토비",
            profileImage = "TOBY"
        )

        val error = result.exceptionOrNull()
        assertTrue(error is AuthException)
        assertTrue((error as AuthException).error is UserAlreadyExistsAuthError)
    }

    @Test
    fun updateProfile_updatesFirestoreAndLocalCache() = runTest {
        val authDataSource = AuthDataSource(InMemorySharedPreferences())
        val userProfileDataSource = FakeUserProfileDataSource(
            profiles = mutableMapOf(
                "uid-1" to UserProfileDto(
                    nickname = "몽이",
                    profileImage = "MONGI",
                    email = "stored@example.com"
                )
            )
        )
        val repository = createRepository(
            authDataSource = authDataSource,
            userProfileDataSource = userProfileDataSource
        )

        repository.updateProfile(nickname = "루나", profileImage = "LUNA")

        assertEquals(
            UserProfileDto(
                nickname = "루나",
                profileImage = "LUNA",
                email = "stored@example.com"
            ),
            userProfileDataSource.profiles["uid-1"]
        )
        assertEquals("uid-1", authDataSource.getUserId())
        assertEquals("루나", authDataSource.getNickname())
        assertEquals("LUNA", authDataSource.getProfileImage())
    }

    @Test
    fun withdraw_deletesFirestoreProfileAndSignsOut() = runTest {
        val firebaseAuthDataSource = FakeFirebaseAuthDataSource()
        val userProfileDataSource = FakeUserProfileDataSource(
            profiles = mutableMapOf(
                "uid-1" to UserProfileDto(
                    nickname = "몽이",
                    profileImage = "MONGI"
                )
            )
        )
        val repository = createRepository(
            firebaseAuthDataSource = firebaseAuthDataSource,
            userProfileDataSource = userProfileDataSource
        )

        repository.withdraw()

        assertFalse(userProfileDataSource.profiles.containsKey("uid-1"))
        assertTrue(firebaseAuthDataSource.signOutCalled)
    }

    @Test
    fun checkAuthStatus_usesCachedProfile_whenFirestoreReadFails() = runTest {
        val authDataSource = AuthDataSource(InMemorySharedPreferences())
        authDataSource.saveUserProfile("캐시닉", "DEFAULT")
        val repository = createRepository(
            authDataSource = authDataSource,
            userProfileDataSource = FakeUserProfileDataSource(
                getProfileError = IllegalStateException("offline")
            )
        )

        val state = repository.checkAuthStatus()

        assertTrue(state.isAuthenticated)
        assertEquals("캐시닉", state.user?.nickname)
        assertEquals("DEFAULT", state.user?.profileImage)
        assertEquals("offline", state.error)
    }

    private fun createRepository(
        firebaseAuthDataSource: FakeFirebaseAuthDataSource = FakeFirebaseAuthDataSource(),
        userProfileDataSource: FakeUserProfileDataSource = FakeUserProfileDataSource(),
        authDataSource: AuthDataSource = AuthDataSource(InMemorySharedPreferences())
    ): AuthRepositoryImpl {
        return AuthRepositoryImpl(
            firebaseAuthDataSource = firebaseAuthDataSource,
            userProfileDataSource = userProfileDataSource,
            authDataSource = authDataSource
        )
    }

    private class FakeFirebaseAuthDataSource(
        private var currentFirebaseUser: FirebaseAuthenticatedUser? = FirebaseAuthenticatedUser(
            uid = "uid-1",
            email = "child@example.com",
            displayName = "구글닉"
        )
    ) : FirebaseAuthDataSource {
        var signOutCalled: Boolean = false

        override fun getCurrentUser(): FirebaseAuthenticatedUser? = currentFirebaseUser

        override fun signOut() {
            signOutCalled = true
            currentFirebaseUser = null
        }
    }

    private class FakeUserProfileDataSource(
        val profiles: MutableMap<String, UserProfileDto> = mutableMapOf(),
        private val getProfileError: Exception? = null
    ) : UserProfileDataSource {
        override suspend fun getProfile(uid: String): UserProfileDto? {
            getProfileError?.let { throw it }
            return profiles[uid]
        }

        override suspend fun saveProfile(uid: String, profile: UserProfileDto) {
            profiles[uid] = profile
        }

        override suspend fun deleteProfile(uid: String) {
            profiles.remove(uid)
        }
    }

    private class InMemorySharedPreferences : SharedPreferences {
        private val values = linkedMapOf<String, Any?>()

        override fun getAll(): MutableMap<String, *> = values.toMutableMap()

        override fun getString(key: String, defValue: String?): String? =
            values[key] as? String ?: defValue

        @Suppress("UNCHECKED_CAST")
        override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>? =
            (values[key] as? Set<String>)?.toMutableSet() ?: defValues

        override fun getInt(key: String, defValue: Int): Int = values[key] as? Int ?: defValue

        override fun getLong(key: String, defValue: Long): Long = values[key] as? Long ?: defValue

        override fun getFloat(key: String, defValue: Float): Float = values[key] as? Float ?: defValue

        override fun getBoolean(key: String, defValue: Boolean): Boolean =
            values[key] as? Boolean ?: defValue

        override fun contains(key: String): Boolean = values.containsKey(key)

        override fun edit(): SharedPreferences.Editor = Editor()

        override fun registerOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener?
        ) = Unit

        override fun unregisterOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener?
        ) = Unit

        private inner class Editor : SharedPreferences.Editor {
            private val pending = linkedMapOf<String, Any?>()
            private val removals = mutableSetOf<String>()
            private var clearRequested = false

            override fun putString(key: String, value: String?): SharedPreferences.Editor =
                apply { pending[key] = value }

            override fun putStringSet(
                key: String,
                values: MutableSet<String>?
            ): SharedPreferences.Editor = apply {
                pending[key] = values?.toSet()
            }

            override fun putInt(key: String, value: Int): SharedPreferences.Editor =
                apply { pending[key] = value }

            override fun putLong(key: String, value: Long): SharedPreferences.Editor =
                apply { pending[key] = value }

            override fun putFloat(key: String, value: Float): SharedPreferences.Editor =
                apply { pending[key] = value }

            override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor =
                apply { pending[key] = value }

            override fun remove(key: String): SharedPreferences.Editor =
                apply { removals += key }

            override fun clear(): SharedPreferences.Editor =
                apply { clearRequested = true }

            override fun commit(): Boolean {
                applyChanges()
                return true
            }

            override fun apply() {
                applyChanges()
            }

            private fun applyChanges() {
                if (clearRequested) values.clear()
                removals.forEach(values::remove)
                pending.forEach { (key, value) ->
                    if (value == null) values.remove(key) else values[key] = value
                }
            }
        }
    }
}
