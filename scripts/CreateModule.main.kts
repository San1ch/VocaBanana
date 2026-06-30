import java.io.File
import kotlin.system.exitProcess

/**
 * Android Module Generator Script
 */

// ======================================================
// CONFIG
// ======================================================

val BASE_PACKAGE = "com.san1ch.vocabanana"

// ======================================================
// ARGS
// ======================================================

val scriptArgs = args

if (scriptArgs.size < 2) {
    println("\nERROR: Missing arguments.")
    println(
        """
        Usage:
        kotlinc -script CreateModule.main.kts <type> <path> <deps?>
        
        Example:
        kotlinc -script CreateModule.main.kts feature :feature:reader hilt,compose
        """.trimIndent()
    )
    exitProcess(1)
}

val type = scriptArgs[0]
val rawPath = scriptArgs[1]
val rawDeps = if (scriptArgs.size > 2) scriptArgs[2] else ""

// ======================================================
// PATHS
// ======================================================

val currentDir = File(".").absoluteFile

val basePath = rawPath
    .trim(':')
    .replace(':', '/')

val packageSuffix = rawPath
    .trim(':')
    .replace(':', '.')

val basePackageName = "$BASE_PACKAGE.$packageSuffix"

// ======================================================
// SUBMODULES
// ======================================================

val subModules = when (type) {
    "feature", "3" -> listOf("domain", "presentation")
    else -> listOf("")
}

// ======================================================
// TEMPLATE
// ======================================================

val templateName = when (type) {
    "android-lib", "1" -> "android-library"
    "kotlin-lib", "2" -> "kotlin-library"
    "feature", "3" -> "feature"

    else -> {
        println("ERROR: Unknown module type")
        exitProcess(1)
    }
}

val templateDir = File(currentDir, "templates/$templateName")

if (!templateDir.exists()) {
    println("ERROR: Template '$templateName' missing")
    exitProcess(1)
}

// ======================================================
// DEPENDENCIES
// ======================================================

val requestedDeps = rawDeps
    .split(",")
    .map { it.trim().lowercase() }
    .filter { it.isNotEmpty() }

val pluginMap = mapOf(
    "hilt" to """
        alias(libs.plugins.hilt)
        alias(libs.plugins.ksp)
    """.trimIndent(),

    "serialization" to """
        alias(libs.plugins.kotlin.serialization)
    """.trimIndent(),

    "parcelize" to """
        id("kotlin-parcelize")
    """.trimIndent()
)

val dependencyMap = mapOf(

    "compose" to """
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.compose.ui)
        implementation(libs.androidx.compose.material3)
    """.trimIndent(),

    "hilt" to """
        implementation(libs.google.hilt)
        ksp(libs.google.hilt.compiler)
    """.trimIndent(),

    "nav" to """
        implementation(libs.androidx.navigation.compose)
    """.trimIndent(),

    "retrofit" to """
        implementation(libs.retrofit)
        implementation(libs.retrofit.converter.gson)
    """.trimIndent()
)

val pluginBlock = requestedDeps
    .mapNotNull { pluginMap[it] }
    .joinToString("\n")

val dependencyBlock = requestedDeps
    .mapNotNull { dependencyMap[it] }
    .joinToString("\n\n")

// ======================================================
// GENERATION
// ======================================================

println("\n🚀 Starting generation: $basePath")

subModules.forEach { subModule ->

    val moduleRelativePath =
        if (subModule.isEmpty()) {
            basePath
        } else {
            "$basePath/$subModule"
        }

    val targetDir = File(currentDir, moduleRelativePath)

    val modulePackage =
        if (subModule.isEmpty()) {
            basePackageName
        } else {
            "$basePackageName.$subModule"
        }

    if (targetDir.exists()) {
        println("SKIP: Already exists -> $moduleRelativePath")
        return@forEach
    }

    val sourceTemplate =
        if (subModule.isNotEmpty()) {
            File(templateDir, subModule)
        } else {
            templateDir
        }

    if (!sourceTemplate.exists()) {
        println("WARNING: Missing sub-template '$subModule'")
        targetDir.mkdirs()
    } else {
        sourceTemplate.copyRecursively(targetDir)
    }

    // ==================================================
    // PROCESS FILES
    // ==================================================

    targetDir.walkTopDown().forEach { file ->

        if (file.name == "build.gradle.kts") {

            val updated = file.readText()
                .replace("_namespace_", modulePackage)
                .replace("// __PLUGINS__", pluginBlock)
                .replace("// __DEPENDENCIES__", dependencyBlock)

            file.writeText(updated)
        }

        if (file.isDirectory && file.name == "java") {

            File(
                file,
                modulePackage.replace('.', '/')
            ).mkdirs()
        }
    }

    // ==================================================
    // SETTINGS.GRADLE
    // ==================================================

    val settingsFile = File(currentDir, "settings.gradle.kts")

    if (settingsFile.exists()) {

        val gradlePath =
            ":" + moduleRelativePath.replace('/', ':')

        val includeLine =
            """include("$gradlePath")"""

        val content = settingsFile.readText()

        if (!content.contains(includeLine)) {
            settingsFile.appendText("\n$includeLine")
        }
    }
}

println("\n✅ GENERATION COMPLETE")
