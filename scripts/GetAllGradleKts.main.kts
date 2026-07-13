#!/usr/bin/env kotlin

import java.io.File

val projectRoot = File(".").canonicalFile
val outputDir = File(projectRoot, "scripts").apply { mkdirs() }
val outputFile = File(outputDir, "build-gradle-report.txt")

val ignoredDirectories = setOf(
    ".git",
    ".gradle",
    ".idea",
    "build"
)

outputFile.printWriter().use { out ->

    projectRoot.walkTopDown()
        .filter { file ->
            file.isFile &&
                    file.name == "build.gradle.kts" &&
                    file.relativeTo(projectRoot)
                        .path
                        .split(File.separator)
                        .none { it in ignoredDirectories }
        }
        .sortedBy { it.relativeTo(projectRoot).path }
        .forEach { file ->

            out.println("=".repeat(100))
            out.println(file.relativeTo(projectRoot).path)
            out.println("=".repeat(100))
            out.println()

            out.println(file.readText())

            out.println()
            out.println()
        }
}

println("Saved to:")
println(outputFile.absolutePath)