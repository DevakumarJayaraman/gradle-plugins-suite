package com.gradle.tooling.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * Task that checks for direct dependency version declarations in build files.
 * Enforces the use of version catalogs (libs.* aliases).
 */
abstract class VerifyNoDirectVersionsTask : DefaultTask() {

    init {
        group = "verification"
        description = "Ensures no direct dependency versions are declared (enforce version catalog usage)."
    }

    @TaskAction
    fun verify() {
        // Detects string notations with explicit version (group:artifact:version) and
        // also detects two-segment notations without a version (group:artifact).
        // Supports both single and double quoted strings and common dependency configurations.
        val forbiddenPatterns = listOf(
            // three-segment with version like "group:artifact:1.2.3"
            Regex("""(implementation|api|compileOnly|runtimeOnly|testImplementation|testRuntimeOnly|testCompileOnly|testApi|compile|testCompile|kapt|kaptTest)\s*\(\s*['"][^'"]+:[^'"]+:[^'"]+['"]\s*\)"""),
            // two-segment without version like "group:artifact"
            Regex("""(implementation|api|compileOnly|runtimeOnly|testImplementation|testRuntimeOnly|testCompileOnly|testApi|compile|testCompile|kapt|kaptTest)\s*\(\s*['"][^'"]+:[^'"]+['"]\s*\)"""),
            // Also cover Groovy style without parentheses (rare but possible in .gradle): implementation 'group:artifact:1.0'
            Regex("""(implementation|api|compileOnly|runtimeOnly|testImplementation|testRuntimeOnly|testCompileOnly|testApi|compile|testCompile|kapt|kaptTest)\s+['"][^'"]+:[^'"]+:[^'"]+['"]"""),
            // Groovy style without version: implementation 'group:artifact'
            Regex("""(implementation|api|compileOnly|runtimeOnly|testImplementation|testRuntimeOnly|testCompileOnly|testApi|compile|testCompile|kapt|kaptTest)\s+['"][^'"]+:[^'"]+['"]""")
        )

        val gradleFiles = project.rootDir
            .walkTopDown()
            .filter { it.isFile && (it.name.endsWith(".gradle.kts") || it.name.endsWith(".gradle")) }

        val violations = mutableListOf<String>()

        gradleFiles.forEach { file ->
            val content = file.readText()
            forbiddenPatterns.forEach { pattern ->
                if (pattern.containsMatchIn(content)) {
                    violations.add("❌ Found direct version in ${file.relativeTo(project.rootDir)}")
                }
            }
        }

        if (violations.isNotEmpty()) {
            violations.forEach { logger.error(it) }
            throw GradleException("🚫 Found ${violations.size} direct dependency version(s). Use libs.* aliases instead.")
        } else {
            logger.lifecycle("✅ No direct dependency versions found — build is clean!")
        }
    }
}
