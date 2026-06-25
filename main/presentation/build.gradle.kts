plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    kotlin("kapt")
}

android {
    namespace = "com.ssafy.jjongle.main.presentation"
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

    buildFeatures {
        compose = true
    }

    composeCompiler {
        stabilityConfigurationFile.set(rootProject.layout.projectDirectory.file("compose_stability.conf"))
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation(project(":common:entity"))
    implementation(project(":common:domain"))
    implementation(project(":common:presentation"))
    api(project(":main:domain"))
    implementation(project(":oxgame:domain"))
    implementation(project(":tangram:domain"))
    implementation(project(":oxgame:presentation"))
    implementation(project(":tangram:presentation"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.animation)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.navigation3.runtime)
    implementation(libs.navigation3.ui)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.viewmodel.compose)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.core)

    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation("com.google.mlkit:pose-detection:18.0.0-beta5")

    implementation(libs.play.services.auth)
    implementation("com.airbnb.android:lottie-compose:6.6.7")

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
}
