import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

class CustomAndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")
            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            val localProperties = Properties()
            val localPropertiesFile = rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                FileInputStream(localPropertiesFile).use { localProperties.load(it) }
            }

            extensions.configure<ApplicationExtension> {
                compileSdk = Const.TargetSdk

                defaultConfig {
                    minSdk = Const.MinSdk
                    targetSdk = Const.TargetSdk
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                signingConfigs {
                    create("release") {
                        val ciStoreFile = System.getenv("KEYSTORE_PATH")
                        val ciStorePassword = System.getenv("KEYSTORE_PASSWORD")
                        val ciKeyAlias = System.getenv("KEY_ALIAS")
                        val ciKeyPassword = System.getenv("KEY_PASSWORD")

                        if (!ciStoreFile.isNullOrEmpty()) {
                            storeFile = file(ciStoreFile)
                            storePassword = ciStorePassword
                            keyAlias = ciKeyAlias
                            keyPassword = ciKeyPassword
                        } else {
                            val localStoreStorePath = localProperties.getProperty("signing.storeFile")
                            if (!localStoreStorePath.isNullOrEmpty()) {
                                storeFile = file(localStoreStorePath)
                                storePassword = localProperties.getProperty("signing.storePassword")
                                keyAlias = localProperties.getProperty("signing.keyAlias")
                                keyPassword = localProperties.getProperty("signing.keyPassword")
                            }
                        }
                    }
                }

                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = true
                        signingConfig = signingConfigs.getByName("release")
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                    getByName("debug") {
                        applicationIdSuffix = ".debug"
                        resValue("string", "app_name", "Vocab (Debug)")
                    }
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }

                buildFeatures {
                    buildConfig = true
                    compose = true
                }
            }

            extensions.configure<KotlinAndroidProjectExtension> {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }
        }
    }
}