#!/usr/bin/env kotlin
import java.io.File

/**
 * ===================== PRESENTATION GENERATOR =====================
 *
 * ✔ Створює:
 *   - <Feature>Screen.kt
 *   - <Feature>ScreenViewModel.kt
 *
 * ✔ Куди:
 *   feature/<feature>/presentation/
 *
 * ✔ Використання:
 *
 *   kotlinc -script PresentationCreator.main.kts -- Home --to=Main
 *
 *   або:
 *
 *   kotlinc -script PresentationCreator.main.kts -- AddText to Main
 *
 * ✔ Результат:
 *   feature/main/presentation/
 *     ├── HomeScreen.kt
 *     └── HomeScreenViewModel.kt
 *
 * ================================================================
 */

// ===================== CONFIG =====================

val projectRoot = File("..")
val templateDir = File(projectRoot, "templates/feature_template_name/presentation")

// ===================== ARGS =====================

val argsClean = args.map { it.trim() }

// Screen name (наприклад: Home, AddText)
val screenName = argsClean.firstOrNull { !it.startsWith("-") }
    ?: error("Screen name required")

// Підтримка двох варіантів:
// --to=Main
// to Main
val targetFeature =
    argsClean.firstOrNull { it.startsWith("--to=", true) }?.substringAfter("=")
        ?: run {
            val i = argsClean.indexOfFirst { it.equals("to", true) }
            if (i != -1 && i + 1 < argsClean.size) argsClean[i + 1] else null
        }
        ?: error("Feature is required. Use --to=FeatureName or to FeatureName")

val pascalName = screenName.replaceFirstChar { it.uppercase() }
val featureFolder = targetFeature.lowercase()

val outputDir = File(
    projectRoot,
    "app/src/main/java/com/example/vocabanana/feature/$featureFolder/presentation"
)

// ===================== GENERATION =====================

fun generate(file: File) {

    val newName = when {
        file.name.contains("feature_template_name") ->
            file.name.replace("feature_template_name", pascalName)

        file.extension == "kt" && !file.name.startsWith(pascalName) ->
            "$pascalName${file.name}"

        else -> file.name
    }

    val targetFile = File(outputDir, newName)

    if (targetFile.exists()) {
        println("[-] Exists: ${targetFile.path}")
        return
    }

    val content = file.readText()
        .replace("<feature>", pascalName)
        .replace("<low_feature>", featureFolder)

    targetFile.parentFile.mkdirs()
    targetFile.writeText(content)

    println("[+] Created: ${targetFile.path}")
}

// ===================== MAIN =====================

println("--- GENERATE SCREEN [$pascalName] -> [$featureFolder] ---")

if (!templateDir.exists()) {
    error("Template not found: ${templateDir.path}")
}

templateDir.walkTopDown().forEach { file ->
    if (!file.isFile) return@forEach

    generate(file)
}

println("--- DONE ---")