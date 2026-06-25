plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(project(":common:domain"))
    api(project(":tangram:entity"))

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
}
