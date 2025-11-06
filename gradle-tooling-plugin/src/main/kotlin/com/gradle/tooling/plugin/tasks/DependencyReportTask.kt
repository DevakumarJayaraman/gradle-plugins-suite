package com.gradle.tooling.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Task that displays all dependencies in the project.
 * Useful for auditing and understanding dependency tree.
 */
abstract class DependencyReportTask : DefaultTask() {

    init {
        group = "tooling"
        description = "Displays a summary of project dependencies"
    }

    @TaskAction
    fun execute() {
        logger.lifecycle("📦 Dependency Report for ${project.name}")
        logger.lifecycle("=" .repeat(50))

        val configurations = project.configurations.filter { it.isCanBeResolved }

        if (configurations.isEmpty()) {
            logger.lifecycle("No resolvable configurations found")
            return
        }

        configurations.forEach { config ->
            try {
                val deps = config.resolvedConfiguration.resolvedArtifacts
                if (deps.isNotEmpty()) {
                    logger.lifecycle("")
                    logger.lifecycle("Configuration: ${config.name}")
                    logger.lifecycle("-" .repeat(50))
                    deps.forEach { artifact ->
                        logger.lifecycle("  • ${artifact.moduleVersion.id}")
                    }
                }
            } catch (e: Exception) {
                logger.debug("Could not resolve configuration ${config.name}: ${e.message}")
            }
        }

        logger.lifecycle("")
        logger.lifecycle("=" .repeat(50))
        logger.lifecycle("✅ Dependency report completed")
    }
}

