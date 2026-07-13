allprojects {
    configurations.all {
        resolutionStrategy {
            force("com.squareup:javapoet:1.13.0")
        }
    }
}

tasks.register("checkEverything") {
    group = "verification"
    description = "Runs lint, ktlint, and unit tests for all modules."

    dependsOn(subprojects.mapNotNull { it.tasks.findByName("spotlessApply") })

    dependsOn(subprojects.mapNotNull { it.tasks.findByName("check") })
}