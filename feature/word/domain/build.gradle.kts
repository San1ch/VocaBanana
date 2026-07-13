plugins {
    alias(libs.plugins.custom.kotlin.library)
    alias(libs.plugins.custom.spotless)
}

dependencies {
    implementation(projects.core.essentials)
}
