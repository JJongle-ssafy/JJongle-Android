plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    kotlin("kapt")
}

android {
    namespace = "com.ssafy.jjongle.tangram.presentation"
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
    implementation(project(":tangram:domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.animation)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.core)
    implementation(libs.kotlinx.collections.immutable)
    implementation("com.airbnb.android:lottie-compose:6.6.7")

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
}
