#!/usr/bin/env kotlin
import java.io.File

// ===================== CONFIG =====================

val projectRoot = File("..")
val templateDir = File(projectRoot, "templates/feature_template_name")

val dbFile = File(projectRoot, "app/src/main/java/com/example/vocabanana/core/repository/AppDatabase.kt")
val moduleFile = File(projectRoot, "app/src/main/java/com/example/vocabanana/core/repository/VocabDatabaseModule.kt")

// ===================== ARGUMENTS =====================

val argsClean = args.map { it.trim() }

val entityName = argsClean.firstOrNull()
    ?: error("Entity name is required")

val toFeature =
    argsClean.firstOrNull { it.startsWith("to=", true) }?.substringAfter("=")
        ?: run {
            val index = argsClean.indexOfFirst { it.equals("to", true) }
            if (index != -1 && index + 1 < argsClean.size) argsClean[index + 1] else null
        }

val isRoom = argsClean.any { it.equals("--room", true) }
val isAddMode = !toFeature.isNullOrBlank()

val entityPascal = entityName.replaceFirstChar { it.uppercase() }
val featureName = toFeature ?: entityName
val featureFolder = featureName.lowercase()

val outputDir = File(
    projectRoot,
    "app/src/main/java/com/example/vocabanana/feature/$featureFolder"
)

val domainFileCheck = File(outputDir, "domain/${entityPascal}Domain.kt")

// Mode detection:
// - Upgrade: domain exists and Room requested → add only Room layer
// - Fresh Room: Room requested and domain does not exist → generate full stack
val isUpgradeMode = isRoom && domainFileCheck.exists()
val isFreshRoomMode = isRoom && !domainFileCheck.exists()

// ===================== HELPERS =====================

/**
 * Converts PascalCase to lowerCamelCase (TextElement → textElement)
 */
fun lowerCamelCase(name: String): String {
    return name.replaceFirstChar { it.lowercase() }
}

/**
 * Safely inserts import after the last existing import
 */
fun addImport(content: String, importLine: String): String {
    if (content.contains(importLine)) return content

    val lastImportIndex = content.lastIndexOf("import ")
    return if (lastImportIndex != -1) {
        val insertIndex = content.indexOf("\n", lastImportIndex) + 1
        content.substring(0, insertIndex) + importLine + "\n" + content.substring(insertIndex)
    } else {
        "$importLine\n$content"
    }
}

/**
 * Inserts entity into AppDatabase using marker [ENTITIES_END]
 * This avoids breaking the Kotlin list structure
 */
fun insertEntity(db: String, entity: String): String {
    val entityEntry = "        $entity::class,"

    if (db.contains(entityEntry)) return db

    return db.replace(
        "// [ENTITIES_END]",
        "$entityEntry\n        // [ENTITIES_END]"
    )
}

// ===================== GENERATION RULES =====================

fun shouldGenerate(relPath: String, file: File): Boolean {

    val name = file.name

    val isDomain = relPath.contains("domain")

    val isRepository =
        name.contains("Repository") ||
                name.contains("Impl")

    val isRoomLayer =
        relPath.contains("data") ||
                relPath.contains("local") ||
                name.contains("Dao") ||
                name.contains("Entity")

    val isModule =
        relPath.contains("module")

    // Upgrade mode: add only Room-related layers to existing domain
    if (isUpgradeMode) {
        return isRoomLayer || isRepository || isModule
    }

    // No Room: generate only domain layer
    if (!isRoom) {
        if (isRepository) return false
        if (isRoomLayer) return false
        if (isModule) return false
        return isDomain
    }

    // Fresh Room: generate full feature stack
    if (isFreshRoomMode) return true

    return false
}

// ===================== FILE GENERATION =====================

fun generate(file: File, relPath: String) {

    val newName = when {
        file.name.contains("feature_template_name") ->
            file.name.replace("feature_template_name", entityPascal)

        file.extension == "kt" && !file.name.startsWith(entityPascal) ->
            "$entityPascal${file.name}"

        else -> file.name
    }

    val target = File(outputDir, "$relPath/$newName")

    if (target.exists()) {
        println("[-] Exists: ${target.path}")
        return
    }

    val content = file.readText()
        .replace("<feature>", entityPascal)
        .replace("<low_feature>", featureFolder)

    target.parentFile.mkdirs()
    target.writeText(content)

    println("[+] Created: ${target.path}")
}

// ===================== ROOM INJECTION =====================

fun injectRoom(name: String, folder: String) {
    if (!dbFile.exists() || !moduleFile.exists()) return

    val dao = "${name}Dao"
    val entity = "${name}Entity"
    val method = "${lowerCamelCase(name)}Dao"

    val daoImport = "import com.example.vocabanana.feature.$folder.data.local.$dao"
    val entityImport = "import com.example.vocabanana.feature.$folder.data.local.$entity"

    var db = dbFile.readText()

    // Add imports
    db = addImport(db, daoImport)
    db = addImport(db, entityImport)

    // Insert entity into database
    db = insertEntity(db, entity)

    // Insert DAO method
    val daoLine = "abstract fun $method(): $dao"
    if (!db.contains(daoLine)) {
        db = db.replace(
            "// [DAOS_END]",
            "$daoLine\n    // [DAOS_END]"
        )
    }

    dbFile.writeText(db)

    // ================= MODULE =================

    var module = moduleFile.readText()

    module = addImport(module, daoImport)

    if (!module.contains("provide$dao")) {
        module = module.replace(
            "// [PROVIDES_END]",
            """
            
    @Provides
    @Singleton
    fun provide$dao(appDatabase: AppDatabase): $dao {
        return appDatabase.$method()
    }
    // [PROVIDES_END]
            """.trimIndent()
        )
    }

    moduleFile.writeText(module)
}

// ===================== MAIN =====================

println(
    when {
        isUpgradeMode -> "--- UPGRADE [$entityPascal] WITH ROOM ---"
        isFreshRoomMode -> "--- CREATE [$entityPascal] WITH ROOM ---"
        isAddMode -> "--- ADD DOMAIN [$entityPascal] -> [$featureName] ---"
        else -> "--- NEW FEATURE [$featureName] ---"
    }
)

templateDir.walkTopDown().forEach { file ->
    if (!file.isFile) return@forEach

    val rel = file.parentFile
        .relativeTo(templateDir)
        .path
        .replace("feature_template_name", "")
        .trim(File.separatorChar)

    if (shouldGenerate(rel, file)) {
        generate(file, rel)
    }
}

if (isRoom) {
    injectRoom(entityPascal, featureFolder)
}

println("--- DONE ---")