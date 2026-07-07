import java.io.File
import kotlin.system.exitProcess

/**
 * ============================================================================
 * ANDROID & KOTLIN MULTI-MODULE GENERATOR
 * ============================================================================
 *
 * DESCRIPTION:
 * A clean, automated script to generate standardized Gradle modules for the
 * Vocabanana Android project ecosystem. It handles template copying, package
 * directory creation (main/test), build.gradle.kts token replacement, and
 * automatic injection into settings.gradle.kts.
 *
 * USAGE:
 * Run this script from the project root directory using the following format:
 * * kotlinc -script ./scripts/CreateModule.main.kts <type> <path> [dependencies]
 *
 * PARAMETERS:
 * - <type>         : The module archetype. Options: 'android-lib' (1), 'kotlin-lib' (2), 'feature' (3)
 * - <path>         : The Gradle-formatted path (e.g., :core:network or :feature:auth or :navigation)
 * - [dependencies] : Optional comma-separated list of catalog tags (e.g., hilt,compose,nav)
 *
 * EXAMPLES:
 * * kotlinc -script ./scripts/CreateModule.main.kts kotlin-lib core:fake
 * * kotlinc -script ./scripts/CreateModule.main.kts android-lib core:ui compose,hilt
 * * kotlinc -script ./scripts/CreateModule.main.kts feature feature:reader compose,hilt,nav
 * ============================================================================
 */

// ============================================================================
// CONFIGURATION & GLOBAL PROPERTIES
// ============================================================================

val basePackage = "com.san1ch.vocabanana"

// Module Types
val typeAndroidLib = "android-lib"
val typeKotlinLib = "kotlin-lib"
val typeFeature = "feature"

// Numeric Aliases
val aliasAndroidLib = "1"
val aliasKotlinLib = "2"
val aliasFeature = "3"

// Placeholders for build.gradle.kts Replacement
val placeholderNamespace = "_namespace_"
val placeholderPlugins = "// __PLUGINS__"
val placeholderDependencies = "// __DEPENDENCIES__"

// Dependency Catalog Configuration
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

// ============================================================================
// DATA MODELS
// ============================================================================

data class ScriptParameters(
    val type: String,
    val rawPath: String,
    val rawDependencies: String
)

data class ModuleContext(
    val type: String,
    val basePath: String,
    val basePackageName: String,
    val templateDir: File,
    val subModules: List<String>,
    val pluginBlock: String,
    val dependencyBlock: String
)

data class SubModuleTarget(
    val relativePath: String,
    val targetDir: File,
    val packageName: String,
    val sourceTemplateDir: File
)

// ============================================================================
// MAIN SCRIPT EXECUTION LOOP
// ============================================================================

val currentDir = File(".").absoluteFile

val parameters = parseArguments(args)
val context = resolveModuleContext(parameters, currentDir)

println("\n🚀 Starting generation for core path: ${context.basePath}")

context.subModules.forEach { subModule ->
    val target = resolveSubModuleTarget(subModule, context, currentDir)

    if (target.targetDir.exists()) {
        println("  ⚠️  SKIP: Already exists -> ${target.relativePath}")
        return@forEach
    }

    println("  📦 Processing target submodule: ${target.relativePath.ifEmpty { "root" }}")

    copyTemplate(target)
    createPackageStructure(target, context.type, subModule)
    updateBuildGradle(target, context.pluginBlock, context.dependencyBlock)
    updateSettingsGradle(target, currentDir)
}

println("\n✅ GENERATION COMPLETE")

// ============================================================================
// HELPER FUNCTIONS IMPLEMENTATION
// ============================================================================

fun parseArguments(args: Array<String>): ScriptParameters {
    if (args.size < 2) {
        println("\n❌ ERROR: Missing arguments.")
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
    val type = args[0]
    val rawPath = args[1]
    val rawDeps = if (args.size > 2) args[2] else ""
    return ScriptParameters(type, rawPath, rawDeps)
}

fun resolveModuleContext(params: ScriptParameters, currentDir: File): ModuleContext {
    val templateName = when (params.type) {
        typeAndroidLib, aliasAndroidLib -> "android-library"
        typeKotlinLib, aliasKotlinLib -> "kotlin-library"
        typeFeature, aliasFeature -> "feature"
        else -> {
            println("❌ ERROR: Unknown module type '${params.type}'")
            exitProcess(1)
        }
    }

    val templateDir = File(currentDir, "templates/$templateName")
    if (!templateDir.exists()) {
        println("❌ ERROR: Template '$templateName' missing at path: ${templateDir.absolutePath}")
        exitProcess(1)
    }

    val basePath = params.rawPath.trim(':').replace(':', '/')
    val packageSuffix = params.rawPath.trim(':').replace(':', '.')
    val basePackageName = "$basePackage.$packageSuffix"

    val subModules = when (params.type) {
        typeFeature, aliasFeature -> listOf("domain", "presentation")
        else -> listOf("")
    }

    val requestedDeps = params.rawDependencies
        .split(",")
        .map { it.trim().lowercase() }
        .filter { it.isNotEmpty() }

    val pluginBlock = requestedDeps
        .mapNotNull { pluginMap[it] }
        .joinToString("\n")

    val dependencyBlock = requestedDeps
        .mapNotNull { dependencyMap[it] }
        .joinToString("\n\n")

    return ModuleContext(
        type = params.type,
        basePath = basePath,
        basePackageName = basePackageName,
        templateDir = templateDir,
        subModules = subModules,
        pluginBlock = pluginBlock,
        dependencyBlock = dependencyBlock
    )
}

fun resolveSubModuleTarget(
    subModule: String,
    context: ModuleContext,
    currentDir: File
): SubModuleTarget {
    val relativePath = if (subModule.isEmpty()) {
        context.basePath
    } else {
        "${context.basePath}/$subModule"
    }

    val targetDir = File(currentDir, relativePath)

    val packageName = if (subModule.isEmpty()) {
        context.basePackageName
    } else {
        "${context.basePackageName}.$subModule"
    }

    val sourceTemplateDir = if (subModule.isNotEmpty()) {
        File(context.templateDir, subModule)
    } else {
        context.templateDir
    }

    return SubModuleTarget(
        relativePath = relativePath,
        targetDir = targetDir,
        packageName = packageName,
        sourceTemplateDir = sourceTemplateDir
    )
}

fun copyTemplate(target: SubModuleTarget) {
    if (!target.sourceTemplateDir.exists()) {
        println("     └─ 🗀 Creating empty target directory (missing structural template: '${target.sourceTemplateDir.name}')")
        target.targetDir.mkdirs()
    } else {
        target.sourceTemplateDir.copyRecursively(target.targetDir)
    }
}

fun createPackageStructure(target: SubModuleTarget, moduleType: String, subModule: String) {
    val packagePath = target.packageName.replace('.', '/')

    // Always generate the main source set package directory
    val mainJavaDir = File(target.targetDir, "src/main/java/$packagePath")
    mainJavaDir.mkdirs()
    println("     └─ 📂 Created Main package: src/main/java/$packagePath")

    // Generate test source set package directory under specified criteria
    val shouldCreateTestDir = moduleType == typeKotlinLib ||
            moduleType == aliasKotlinLib ||
            subModule == "domain"

    if (shouldCreateTestDir) {
        val testJavaDir = File(target.targetDir, "src/test/java/$packagePath")
        testJavaDir.mkdirs()
        println("     └─ 📂 Created Test package: src/test/java/$packagePath")
    }
}

fun updateBuildGradle(target: SubModuleTarget, pluginBlock: String, dependencyBlock: String) {
    target.targetDir.walkTopDown().forEach { file ->
        if (file.name == "build.gradle.kts") {
            val updatedContent = file.readText()
                .replace(placeholderNamespace, target.packageName)
                .replace(placeholderPlugins, pluginBlock)
                .replace(placeholderDependencies, dependencyBlock)

            file.writeText(updatedContent)
            println("     └─ ⚙️  Updated build.gradle.kts placeholders")
        }
    }
}

fun updateSettingsGradle(target: SubModuleTarget, currentDir: File) {
    val settingsFile = File(currentDir, "settings.gradle.kts")
    if (!settingsFile.exists()) return

    val gradlePath = ":" + target.relativePath.replace('/', ':')
    val includeLine = """include("$gradlePath")"""

    val content = settingsFile.readText()
    if (!content.contains(includeLine)) {
        settingsFile.appendText("\n$includeLine")
        println("     └─ 📝 Added inclusion entry to settings.gradle.kts")
    }
}