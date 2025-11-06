package com.gradle.dependency.plugin

import org.gradle.api.*
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.*
import java.io.File
import java.net.URI

class DependencyPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val pipelineType = (project.findProperty("pipelineType") as? String)?.uppercase() ?: "SERVICE"
        project.logger.lifecycle("🔧 Applying gradle-dependency-plugin for ${project.name} (pipelineType=$pipelineType)")

        configureRepositories(project)

        project.pluginManager.apply("java-library")
        project.pluginManager.apply("org.jetbrains.kotlin.jvm")

        if (pipelineType == "SERVICE") {
            project.pluginManager.apply("org.springframework.boot")
            project.logger.lifecycle("✅ SERVICE detected: Boot plugin applied")
        } else {
            project.logger.lifecycle("📦 LIB detected: Boot plugin skipped")
        }

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
                val catalogFiles = settings.providers.provider { listOf(generated) }
                create("libs") {from(catalogFiles.get())}
            }
        }
        println("📘 Version catalog applied from plugin resource.")
    }
}
