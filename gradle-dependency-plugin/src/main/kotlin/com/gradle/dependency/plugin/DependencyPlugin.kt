package com.gradle.dependency.plugin

import org.gradle.api.*
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.*
import java.io.File
import java.net.URI

/**
 * Convention plugin that provides standardized dependency management, repository configuration,
 * and build setup for Java/Kotlin projects.
 *
 * This plugin centralizes:
 * - Plugin application (Kotlin, Spring Boot based on pipelineType)
 * - Repository configuration
 * - Version catalog distribution
 * - Java toolchain and test framework setup
 *
 * Consumers only need to apply this single plugin - all required plugins are applied automatically.
 */
class DependencyPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val pipelineType = (project.findProperty("pipelineType") as? String)?.uppercase() ?: "SERVICE"
        project.logger.lifecycle("🔧 Applying gradle-dependency-plugin for ${project.name} (pipelineType=$pipelineType)")

        configureRepositories(project)
        applyRequiredPlugins(project, pipelineType)

        if (project == project.rootProject) {
            project.gradle.settingsEvaluated { applyCatalogFromResource(this) }
            project.subprojects.forEach { configureRepositories(it) }
        }

        project.afterEvaluate {
            if (pipelineType == "LIB") {
                tasks.matching { it.name == "bootJar" }.configureEach { enabled = false }
                tasks.matching { it.name == "jar" }.configureEach { enabled = true }
            }
            extensions.configure<JavaPluginExtension> {
                toolchain.languageVersion.set(JavaLanguageVersion.of(17))
            }
            tasks.withType<Test>().configureEach { useJUnitPlatform() }
        }
    }

    private fun configureRepositories(project: Project) {
        project.repositories.apply {
            clear()
            mavenCentral()
            maven("https://repo.spring.io/release")
            maven {
                url = URI("https://artifactory.myorg.com/libs-release")
                isAllowInsecureProtocol = false
            }
        }
        project.logger.lifecycle("📦 Repositories applied automatically for ${project.name}")
    }

    /**
     * Applies required plugins based on pipeline type.
     * Plugins are applied conditionally to be idempotent.
     */
    private fun applyRequiredPlugins(project: Project, pipelineType: String) {
        // Apply base plugins
        project.pluginManager.apply("java-library")

        // Apply Kotlin plugin if not already applied
        if (!project.pluginManager.hasPlugin("org.jetbrains.kotlin.jvm")) {
            project.pluginManager.apply("org.jetbrains.kotlin.jvm")
            project.logger.lifecycle("✅ Applied Kotlin JVM plugin")
        }

        // Apply Spring Boot for SERVICE pipeline type
        if (pipelineType == "SERVICE") {
            if (!project.pluginManager.hasPlugin("org.springframework.boot")) {
                project.pluginManager.apply("org.springframework.boot")
                project.logger.lifecycle("✅ SERVICE detected: Spring Boot plugin applied")
            }
        } else {
            project.logger.lifecycle("📦 LIB detected: Spring Boot plugin skipped")
        }
    }

    private fun applyCatalogFromResource(settings: Settings) {
        val resourcePath = "/catalogs/libs.versions.toml"
        val text = javaClass.getResourceAsStream(resourcePath)?.bufferedReader()?.readText()
            ?: throw GradleException("Could not find $resourcePath in plugin resources")
        val generated = File(settings.rootDir, "build/generated$resourcePath").apply {
            parentFile.mkdirs()
            writeText(text)
        }
        settings.dependencyResolutionManagement {
            @Suppress("UnstableApiUsage")
            repositories { mavenCentral() }
            versionCatalogs {
                create("libs") {
                    from(generated)
                }
            }
        }
        println("📘 Version catalog applied from plugin resource.")
    }
}
