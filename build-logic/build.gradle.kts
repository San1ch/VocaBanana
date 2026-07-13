plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.plugin.ksp)
    implementation(libs.plugin.hilt)

    implementation(libs.plugin.android.application)
    implementation(libs.plugin.android.library)
    implementation(libs.plugin.kotlin.android)
    implementation("org.jetbrains.kotlin.plugin.compose:org.jetbrains.kotlin.plugin.compose.gradle.plugin:2.1.0")
    implementation(libs.plugin.jetbrains.kotlin.jvm)
    implementation(libs.detekt)
    implementation(libs.spotless)

    implementation(libs.javapoet)
    constraints {
        implementation("com.squareup:javapoet:1.13.0") {
            because("Force consistent version to avoid NoSuchMethodError")
        }
    }
}

gradlePlugin {
    plugins {
        register("customAndroidApplication") {
            id = "custom-android-application"
            implementationClass = "CustomAndroidApplicationPlugin"
        }
        register("customAndroidLibrary") {
            id = "custom-android-library"
            implementationClass = "CustomAndroidLibraryPlugin"
        }
        register("customKotlinLibrary") {
            id = "custom-kotlin-library"
            implementationClass = "CustomKotlinLibraryPlugin"
        }
        register("customAndroidComposeLibrary") {
            id = "custom-android-compose-library"
            implementationClass = "CustomAndroidComposeLibraryPlugin"
        }
        register("customSpotless") {
            id = "custom-spotless"
            implementationClass = "CustomSpotlessPlugin"
        }
    }
}