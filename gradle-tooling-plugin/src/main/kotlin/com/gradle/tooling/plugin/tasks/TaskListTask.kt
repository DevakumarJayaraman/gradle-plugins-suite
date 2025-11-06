package com.gradle.tooling.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Task that displays all available tasks in the project.
 * Provides a more detailed view than the standard tasks command.
 */
abstract class TaskListTask : DefaultTask() {

    init {
        group = "tooling"
        description = "Lists all tasks grouped by category"
    }

    @TaskAction
    fun execute() {
        logger.lifecycle("📋 Task List for ${project.name}")
        logger.lifecycle("=" .repeat(50))

        val tasksByGroup = project.tasks.groupBy { it.group ?: "other" }

        tasksByGroup.toSortedMap().forEach { (group, tasks) ->
            logger.lifecycle("")
            logger.lifecycle("${group.uppercase()} tasks")
            logger.lifecycle("-" .repeat(50))

            tasks.sortedBy { it.name }.forEach { task ->
                val description = task.description ?: "No description"
                logger.lifecycle("  ${task.name} - $description")
            }
        }

        logger.lifecycle("")
        logger.lifecycle("=" .repeat(50))
        logger.lifecycle("Total tasks: ${project.tasks.size}")
        logger.lifecycle("✅ Task list completed")
    }
}

