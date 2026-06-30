plugins {
    id("custom-android-application")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.san1ch.vocabanana"
    defaultConfig {
        applicationId = "com.san1ch.vocabanana"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "Vocab (Debug)")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
        compose = false
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies{
    implementation(projects.navigation)

    implementation(projects.core.essentials)
    implementation(projects.core.android.database)
    implementation(projects.core.ui)

    implementation(projects.feature.debug.presentation)
    implementation(projects.feature.init.presentation)
    implementation(projects.feature.main.presentation)
    implementation(projects.feature.mainsettings.presentation)
    implementation(projects.feature.text.presentation)
    implementation(projects.feature.vocabulary.presentation)
    implementation(projects.feature.word.presentation)


    implementation(libs.androidx.activity.compose)

    //hilt for android app
    implementation(libs.google.hilt)
    ksp(libs.google.hilt.compiler)
}