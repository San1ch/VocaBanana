plugins {
    alias(libs.plugins.custom.kotlin.library)
    alias(libs.plugins.custom.spotless)
}

dependencies {
    implementation(projects.core.essentials)
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)
}
