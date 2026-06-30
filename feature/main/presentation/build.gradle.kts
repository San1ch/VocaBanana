plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.san1ch.vocabanana.feature.main.presentation"
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.essentials)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    // Hilt DI
    implementation(libs.google.hilt)
    ksp(libs.google.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // AndroidX & UI
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.android.material)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}