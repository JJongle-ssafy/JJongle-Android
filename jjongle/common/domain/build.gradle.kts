plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(project(":common:entity"))
    api(libs.coroutines.core)
    api(libs.javax.inject)
}
