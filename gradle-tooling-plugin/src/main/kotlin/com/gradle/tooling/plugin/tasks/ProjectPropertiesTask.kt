package com.gradle.tooling.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Task that displays all project properties.
 * Useful for debugging configuration issues.
 */
abstract class ProjectPropertiesTask : DefaultTask() {

    init {
        group = "tooling"
        description = "Displays all project properties and their values"
    }

    @TaskAction
    fun execute() {
        logger.lifecycle("⚙️  Project Properties for ${project.name}")
        logger.lifecycle("=" .repeat(50))

        // Standard properties
        logger.lifecycle("")
        logger.lifecycle("Standard Properties:")
        logger.lifecycle("-" .repeat(50))
        logger.lifecycle("  name: ${project.name}")
        logger.lifecycle("  group: ${project.group}")
        logger.lifecycle("  version: ${project.version}")
        logger.lifecycle("  path: ${project.path}")
        logger.lifecycle("  projectDir: ${project.projectDir}")
        logger.lifecycle("  buildDir: ${project.layout.buildDirectory.get().asFile}")
        logger.lifecycle("  rootDir: ${project.rootDir}")

        // Extra properties
        val extraProps = project.properties.filter {
            it.key !in setOf("name", "group", "version", "path", "projectDir", "buildDir", "rootDir")
        }

        if (extraProps.isNotEmpty()) {
            logger.lifecycle("")
            logger.lifecycle("Custom Properties:")
            logger.lifecycle("-" .repeat(50))
            extraProps.toSortedMap().forEach { (key, value) ->
                logger.lifecycle("  $key: $value")
            }
        }

        // Applied plugins
        logger.lifecycle("")
        logger.lifecycle("Applied Plugins:")
        logger.lifecycle("-" .repeat(50))
        project.plugins.forEach { plugin ->
            logger.lifecycle("  • ${plugin.javaClass.simpleName}")
        }

        logger.lifecycle("")
        logger.lifecycle("=" .repeat(50))
        logger.lifecycle("✅ Properties display completed")
    }
}

