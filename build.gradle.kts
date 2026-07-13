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

    val spotlessApplyTasks = subprojects.mapNotNull { it.tasks.findByName("spotlessApply") }
    val checkTasks = subprojects.mapNotNull { it.tasks.findByName("check") }

    dependsOn(spotlessApplyTasks)
    dependsOn(checkTasks)

    checkTasks.forEach { checkTask ->
        spotlessApplyTasks.forEach { spotlessTask ->
            checkTask.mustRunAfter(spotlessTask)
        }
    }
}