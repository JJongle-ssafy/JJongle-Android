pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "jjongle"
include(":app")
include(":baselineprofile")
include(":tti")
include(":common:entity")
include(":common:domain")
include(":common:data")
include(":common:presentation")
include(":main:entity")
include(":main:domain")
include(":main:data")
include(":main:presentation")
include(":oxgame:entity")
include(":oxgame:domain")
include(":oxgame:data")
include(":oxgame:presentation")
include(":tangram:entity")
include(":tangram:domain")
include(":tangram:data")
include(":tangram:presentation")
