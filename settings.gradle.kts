includeBuild("build-logic")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

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

rootProject.name = "VocaBanana"
include(":app")
include(":navigation")

include(":core:ui")
include(":core:essentials")

include(":core:android:database")
include(":core:android:commonandroid")

include(":feature:init:domain")
include(":feature:init:presentation")

include(":feature:text:domain")
include(":feature:text:presentation")

include(":feature:main:domain")
include(":feature:main:presentation")

include(":feature:debug:domain")
include(":feature:debug:presentation")
include(":feature:mainsettings:domain")
include(":feature:mainsettings:presentation")
include(":feature:vocabulary:domain")
include(":feature:vocabulary:presentation")
include(":feature:word:domain")
include(":feature:word:presentation")