package com.gradle.tooling.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * A simple task that prints a greeting message.
 * Demonstrates basic task creation in Gradle plugins.
 */
abstract class HelloToolingTask : DefaultTask() {

    init {
        group = "tooling"
        description = "Prints a greeting message from the tooling plugin"
    }

    @TaskAction
    fun execute() {
        println("👋 Hello from gradle-tooling-plugin!")
        logger.lifecycle("✅ HelloTooling task completed successfully")
    }
}

