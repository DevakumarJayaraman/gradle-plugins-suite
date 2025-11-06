package com.gradle.tooling.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Task that displays environment and project information.
 * Useful for debugging and understanding the build environment.
 */
abstract class EnvSummaryTask : DefaultTask() {

    init {
        group = "tooling"
        description = "Prints environment and project summary information"
    }

    @TaskAction
    fun execute() {
        logger.lifecycle("📊 Environment Summary")
        logger.lifecycle("=" .repeat(50))

        // Project information
        logger.lifecycle("Project: ${project.name}")
        logger.lifecycle("Group: ${project.group}")
        logger.lifecycle("Version: ${project.version}")
        logger.lifecycle("Path: ${project.path}")
        logger.lifecycle("Build Dir: ${project.layout.buildDirectory.get().asFile}")

        // Gradle information
        logger.lifecycle("")
        logger.lifecycle("Gradle Version: ${project.gradle.gradleVersion}")
        logger.lifecycle("Gradle Home: ${project.gradle.gradleHomeDir}")

        // Java information
        logger.lifecycle("")
        logger.lifecycle("Java Version: ${System.getProperty("java.version")}")
        logger.lifecycle("Java Vendor: ${System.getProperty("java.vendor")}")
        logger.lifecycle("Java Home: ${System.getProperty("java.home")}")

        // System information
        logger.lifecycle("")
        logger.lifecycle("OS Name: ${System.getProperty("os.name")}")
        logger.lifecycle("OS Version: ${System.getProperty("os.version")}")
        logger.lifecycle("OS Arch: ${System.getProperty("os.arch")}")

        logger.lifecycle("=" .repeat(50))
        logger.lifecycle("✅ Environment summary completed")
    }
}

