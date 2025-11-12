package com.gradle.tooling.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * Task that checks for direct dependency version declarations in build files.
 * Enforces the use of version catalogs (libs.* aliases).
 *
 * Behavior change: a dependency declared without a version (e.g. "group:artifact")
 * is allowed if a dependency constraint exists anywhere in the project that provides
 * a version for that group:artifact. Explicit versions in dependency declarations
 * (group:artifact:version) are still forbidden — except when they appear inside
 * a `constraints { ... }` block (those are allowed as they are the intended overrides).
 */
abstract class VerifyNoDirectVersionsTask : DefaultTask() {

    init {
        group = "verification"
        description = "Ensures no direct dependency versions are declared (enforce version catalog usage)."
    }

    // Scans content for `constraints { ... }` blocks and returns a pair of:
    //  - Set of constrained group:artifact coordinates (from strings with versions inside the block)
    //  - List of IntRange ranges (start..end indices) for each constraints block found in the content
    private fun findConstrainedModulesAndRanges(content: String): Pair<Set<String>, List<IntRange>> {
        val result = mutableSetOf<String>()
        val ranges = mutableListOf<IntRange>()
        var searchIndex = 0
        while (true) {
            val idx = content.indexOf("constraints", searchIndex)
            if (idx == -1) break
            // find the next opening brace after the 'constraints' token
            val braceOpen = content.indexOf('{', idx)
            if (braceOpen == -1) break
            // find matching closing brace (simple brace matcher)
            var i = braceOpen + 1
            var depth = 1
            while (i < content.length && depth > 0) {
                when (content[i]) {
                    '{' -> depth++
                    '}' -> depth--
                }
                i++
            }
            if (depth != 0) break // malformed, stop
            val blockStart = braceOpen
            val blockEnd = i - 1
            ranges.add(blockStart..blockEnd)
            val block = content.substring(braceOpen + 1, i - 1)

            // 1) Find string literals with coordinates that include a version (group:artifact:version)
            // e.g. "org.example:lib:1.2.3" or 'org.example:lib:1.2.3'
            val coordWithVersion = Regex("""['"]([^'":]+:[^'":]+:[^'":]+)['"]""")
            coordWithVersion.findAll(block).forEach { m ->
                val coord = m.groupValues[1]
                val parts = coord.split(":" )
                if (parts.size >= 2) {
                    val ga = parts[0] + ":" + parts[1]
                    result.add(ga)
                }
            }

            // 2) Also detect coordinates declared without version inside constraints, but followed by a version block
            //    Examples handled:
            //    implementation("group:artifact") { version { strictly("1.2.3") } }
            //    implementation("group:artifact") { strictly("1.2.3") }
            val coordWithoutVersion = Regex("""['"]([^'":]+:[^'":]+)['"]""")
            coordWithoutVersion.findAll(block).forEach { m ->
                val coord = m.groupValues[1]
                val parts = coord.split(":")
                if (parts.size >= 2) {
                    val ga = parts[0] + ":" + parts[1]
                    // look ahead in the block after this match for common version indicators
                    val tailStart = m.range.last + 1
                    val tail = if (tailStart < block.length) block.substring(tailStart, minOf(block.length, tailStart + 300)) else ""
                    if ("strictly(" in tail || "version" in tail || "version =" in tail || Regex("""\bstrictly\s*\(""").containsMatchIn(tail)) {
                        result.add(ga)
                    }
                }
            }

            searchIndex = i
        }
        return result to ranges
    }

    @TaskAction
    fun verify() {
        val configs = listOf(
            "implementation",
            "api",
            "compileOnly",
            "runtimeOnly",
            "testImplementation",
            "testRuntimeOnly",
            "testCompileOnly",
            "testApi",
            "compile",
            "testCompile",
            "kapt",
            "kaptTest"
        )

        // Regex that captures the dependency notation inside quotes for Kotlin DSL with parentheses
        val kotlinCallRegex = Regex("""(${configs.joinToString("|")})\s*\(\s*['"]([^'"]+)['"]\s*\)""")
        // Regex that captures the dependency notation inside quotes for Groovy-style declarations without parentheses
        val groovyCallRegex = Regex("""(${configs.joinToString("|")})\s+['"]([^'"]+)['"]""")
        // Regex that detects explicit three-segment notation inside any string (safety)
        val explicitThreeSegment = Regex("""['"]([^'"]+:[^'"]+:[^'"]+)['"]""")

        val gradleFiles = project.rootDir
            .walkTopDown()
            .filter { it.isFile && (it.name.endsWith(".gradle.kts") || it.name.endsWith(".gradle")) }
            .toList()

        // First pass: collect constrained modules across files and constraints block ranges per file
        val constraintsInfoByFile = gradleFiles.associateWith { file ->
            val content = file.readText()
            findConstrainedModulesAndRanges(content)
        }

        val constrainedModules = constraintsInfoByFile.values.flatMap { it.first }.toSet()

        val violations = mutableListOf<String>()

        gradleFiles.forEach { file ->
            val content = file.readText()
            val (_, rangesForFile) = constraintsInfoByFile[file] ?: (emptySet<String>() to emptyList())

            // 1) Find Kotlin-style and Groovy-style dependency string usages (capture match start index)
            val matches = mutableListOf<Pair<String, Int>>()

            kotlinCallRegex.findAll(content).forEach { m ->
                // group 2 is the notation inside the quotes; m.range.first is start index of the match
                matches.add(m.groupValues[2] to m.range.first)
            }
            groovyCallRegex.findAll(content).forEach { m ->
                matches.add(m.groupValues[2] to m.range.first)
            }

            // 2) Evaluate each found notation
            matches.forEach { (notation, matchIndex) ->
                // if this match occurs inside a constraints block for this file, skip (constraints provide versions)
                val inConstraintBlock = rangesForFile.any { it.contains(matchIndex) }
                if (inConstraintBlock) return@forEach

                val parts = notation.split(":")
                if (parts.size >= 3) {
                    // Explicit version provided in dependency declaration -> violation
                    violations.add("❌ Found direct version in ${file.relativeTo(project.rootDir)} -> $notation")
                } else if (parts.size == 2) {
                    // group:artifact without version -> allowed only if a constraint provides the version somewhere
                    val ga = parts[0] + ":" + parts[1]
                    if (!constrainedModules.contains(ga)) {
                        violations.add("❌ Found direct dependency without version in ${file.relativeTo(project.rootDir)} -> $notation (no constraint found)")
                    }
                }
                // other forms (e.g., single token) are ignored by this check
            }

            // 3) Safety: also detect any three-segment explicit strings not captured above (edge cases)
            explicitThreeSegment.findAll(content).forEach { m ->
                val coord = m.groupValues[1]
                val matchIndex = m.range.first
                // If this string sits inside a constraints block for this file, it's allowed
                if (rangesForFile.any { it.contains(matchIndex) }) return@forEach

                // Otherwise, mark it as violation (if not already recorded)
                val marker = "❌ Found direct version in ${file.relativeTo(project.rootDir)} -> $coord"
                if (!violations.contains(marker)) {
                    violations.add(marker)
                }
            }
        }

        if (violations.isNotEmpty()) {
            violations.forEach { logger.error(it) }
            throw GradleException("🚫 Found ${violations.size} direct dependency version(s). Use libs.* aliases or constraints instead.")
        } else {
            logger.lifecycle("✅ No direct dependency versions found — build is clean!")
        }
    }
}
