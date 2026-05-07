plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")

    //DI
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")

    //Json
    id("org.jetbrains.kotlin.plugin.serialization")
}


android {
    compileSdk = Const.TargetSdk
    defaultConfig {
        minSdk = Const.MinSdk
        targetSdk = Const.TargetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),

                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "Vocab (Debug)")
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    kotlinOptions {
        jvmTarget = "11"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}