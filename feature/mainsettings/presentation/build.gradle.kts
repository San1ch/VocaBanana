plugins {
    alias(libs.plugins.custom.android.compose.library)
    alias(libs.plugins.custom.spotless)
}

android {
    namespace = "com.san1ch.vocabanana.feature.mainsettings.presentation"
}

dependencies {

    implementation(projects.core.essentials)
    implementation(projects.core.ui)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.material.icons.extended)

    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.google.hilt)
    ksp(libs.google.hilt.compiler)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.android.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
