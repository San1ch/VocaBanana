plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.plugin.android.application)
    implementation(libs.plugin.kotlin.android)
    implementation(libs.plugin.kotlin.compose)
    implementation(libs.plugin.hilt)
    implementation(libs.plugin.ksp)
    implementation(libs.plugin.kotlin.serialization)
}