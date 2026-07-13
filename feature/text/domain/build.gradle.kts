plugins {
    alias(libs.plugins.custom.kotlin.library)
    alias(libs.plugins.custom.spotless)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(projects.core.essentials)
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(projects.core.essentials)
    testImplementation(libs.javax.inject)
    testImplementation(libs.kotlinx.coroutines.core)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
