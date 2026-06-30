plugins {
    alias(libs.plugins.custom.android.library)
    // __PLUGINS__


}

android {
    namespace = "_namespace_"
}

dependencies {
    // __DEPENDENCIES__

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.android.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}