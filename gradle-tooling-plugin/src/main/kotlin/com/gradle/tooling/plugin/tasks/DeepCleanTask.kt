package com.gradle.tooling.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Task that cleans generated files and build artifacts.
 * More aggressive than standard clean, useful for troubleshooting.
 */
abstract class DeepCleanTask : DefaultTask() {

    init {
        group = "tooling"
        description = "Performs deep clean of build directory and caches"
    }

    @TaskAction
    fun execute() {
        logger.lifecycle("🧹 Deep Clean for ${project.name}")
        logger.lifecycle("=" .repeat(50))

        var deletedCount = 0
        var deletedSize = 0L

        // Clean build directory
        val buildDir = project.layout.buildDirectory.get().asFile
        if (buildDir.exists()) {
            val size = buildDir.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
            logger.lifecycle("Deleting build directory: ${buildDir.absolutePath}")
            if (buildDir.deleteRecursively()) {
                deletedCount++
                deletedSize += size
                logger.lifecycle("  ✅ Deleted (${formatSize(size)})")
            } else {
                logger.warn("  ⚠️  Failed to delete")
            }
        }

        // Clean .gradle directory in project
        val gradleDir = File(project.projectDir, ".gradle")
        if (gradleDir.exists()) {
            val size = gradleDir.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
            logger.lifecycle("Deleting .gradle directory: ${gradleDir.absolutePath}")
            if (gradleDir.deleteRecursively()) {
                deletedCount++
                deletedSize += size
                logger.lifecycle("  ✅ Deleted (${formatSize(size)})")
            } else {
                logger.warn("  ⚠️  Failed to delete")
            }
        }

        logger.lifecycle("")
        logger.lifecycle("=" .repeat(50))
        logger.lifecycle("Deleted $deletedCount directories")
        logger.lifecycle("Freed ${formatSize(deletedSize)}")
        logger.lifecycle("✅ Deep clean completed")
    }

    private fun formatSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
            else -> "${bytes / (1024 * 1024 * 1024)} GB"
        }
    }
}

