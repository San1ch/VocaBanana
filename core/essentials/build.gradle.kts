plugins {
    alias(libs.plugins.custom.kotlin.library)
}

dependencies {
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)
}