plugins {
    alias(libs.plugins.custom.android.compose.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.san1ch.vocabanana.feature.text.presentation"
}

dependencies {
    implementation(projects.core.essentials)
    implementation(projects.core.ui)
    implementation(projects.core.android.commonandroid)
    implementation(projects.feature.text.domain)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)

    implementation(libs.google.hilt)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.google.hilt.compiler)

    implementation(libs.androidx.material.icons.extended)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.android.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}