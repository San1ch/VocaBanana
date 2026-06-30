plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.san1ch.vocabanana.navigation"

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
}

dependencies {
    //Modules
    implementation(projects.core.essentials)
    implementation(projects.core.ui)

    implementation(projects.feature.main.presentation)
    implementation(projects.feature.init.presentation)
    implementation(projects.feature.text.presentation)
    implementation(projects.feature.debug.presentation)
    implementation(projects.feature.vocabulary.presentation)
    implementation(projects.feature.mainsettings.presentation)
    implementation(projects.feature.word.presentation)

    //serialization
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.navigation.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material3)

    implementation(libs.google.hilt)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.google.hilt.compiler)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.android.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}