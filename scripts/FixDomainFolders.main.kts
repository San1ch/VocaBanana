import java.io.File

/**
 * ============================================================================
 * FEATURE DOMAIN PACKAGE FIXER SCRIPT
 * ============================================================================
 * * DESCRIPTION:
 * This script scans the 'feature/' directory at the project root, looks for
 * 'domain' submodules within each feature, and automatically generates the
 * missing 'src/main/java' and 'src/test/java' package folder structures
 * based on the feature's name. It will NOT overwrite or delete any existing files.
 *
 * USAGE:
 * Run this script from the project root directory using the following command:
 * * kotlinc -script ./scripts/FixDomainFolders.main.kts
 * * EXAMPLE GENERATION:
 * For a feature named 'reader' (:feature:reader:domain), it will generate:
 * - feature/reader/domain/src/main/java/com/san1ch/vocabanana/feature/reader/domain/
 * - feature/reader/domain/src/test/java/com/san1ch/vocabanana/feature/reader/domain/
 * ============================================================================
 */

val BASE_PACKAGE = "com.san1ch.vocabanana"

val currentDir = File(".").absoluteFile
val featureDir = File(currentDir, "feature")

if (!featureDir.exists() || !featureDir.isDirectory) {
    println("❌ ERROR: 'feature' directory not found at ${featureDir.absolutePath}")
    System.exit(1)
}

println("🚀 Analyzing features inside: ${featureDir.absolutePath}\n")

// Get all top-level directories inside the 'feature' folder
val features = featureDir.listFiles { file -> file.isDirectory } ?: emptyArray()

var fixedCount = 0

features.forEach { featureFolder ->
    val featureName = featureFolder.name
    val domainDir = File(featureFolder, "domain")

    // Process only if the feature actually contains a 'domain' submodule
    if (domainDir.exists() && domainDir.isDirectory) {

        // Dynamic package construction: com.san1ch.vocabanana.feature.<feature_name>.domain
        val targetPackage = "$BASE_PACKAGE.feature.$featureName.domain"
        val packagePath = targetPackage.replace('.', '/')

        // Target source set directories
        val mainJavaDir = File(domainDir, "src/main/java/$packagePath")
        val testJavaDir = File(domainDir, "src/test/java/$packagePath")

        var structureUpdated = false

        if (!mainJavaDir.exists()) {
            mainJavaDir.mkdirs()
            println("  └─ Created main: src/main/java/$packagePath")
            structureUpdated = true
        }

        if (!testJavaDir.exists()) {
            testJavaDir.mkdirs()
            println("  └─ Created test: src/test/java/$packagePath")
            structureUpdated = true
        }

        if (structureUpdated) {
            println("🛠 Fixed structures for feature: [$featureName]")
            fixedCount++
        }
    }
}

println("\n✅ ANALYSIS COMPLETE. Fixed $fixedCount features.")