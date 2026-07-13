plugins {
    alias(libs.plugins.custom.kotlin.library)
    alias(libs.plugins.custom.spotless)
}

dependencies {
    implementation(projects.core.essentials)

    implementation(libs.kotlinx.coroutines.core)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
