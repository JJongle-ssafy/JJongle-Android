plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    kotlin("kapt")
}

android {
    namespace = "com.ssafy.jjongle.oxgame.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation(project(":oxgame:domain"))
    implementation(project(":common:data"))

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.coroutines.android)
    implementation(libs.coroutines.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
}
