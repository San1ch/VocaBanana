plugins {
    alias(libs.plugins.custom.kotlin.library)
}

dependencies {

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}