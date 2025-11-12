package com.gradle.tooling.plugin

import com.gradle.tooling.plugin.tasks.DeepCleanTask
import com.gradle.tooling.plugin.tasks.DependencyReportTask
import com.gradle.tooling.plugin.tasks.EnvSummaryTask
import com.gradle.tooling.plugin.tasks.HelloToolingTask
import com.gradle.tooling.plugin.tasks.ProjectPropertiesTask
import com.gradle.tooling.plugin.tasks.TaskListTask
import com.gradle.tooling.plugin.tasks.VerifyNoDirectVersionsTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

/**
 * ToolingPlugin provides useful tasks for project development and debugging.
 *
 * This plugin registers several tasks in the "tooling" group:
 * - helloTooling: Simple greeting task
 * - envSummary: Displays environment and project information
 * - dependencyReport: Shows all project dependencies
 * - taskList: Lists all available tasks
 * - projectProperties: Displays all project properties
 * - deepClean: Performs deep clean of build artifacts
 */
class ToolingPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.logger.lifecycle("🔧 Applying gradle-tooling-plugin to ${project.name}")

        // Register HelloTooling task
        project.tasks.register<HelloToolingTask>("helloTooling")

        // Register EnvSummary task
        project.tasks.register<EnvSummaryTask>("envSummary")

        // Register DependencyReport task
        project.tasks.register<DependencyReportTask>("dependencyReport")

        // Register TaskList task
        project.tasks.register<TaskListTask>("taskList")

        // Register ProjectProperties task
        project.tasks.register<ProjectPropertiesTask>("projectProperties")

        // Register DeepClean task
        project.tasks.register<DeepCleanTask>("deepClean")

        project.tasks.register<VerifyNoDirectVersionsTask>("verifyNoDirectVersions")

        project.logger.lifecycle("✅ gradle-tooling-plugin applied successfully")
        project.logger.lifecycle("   Run './gradlew tasks --group tooling' to see available tasks")
    }
}

