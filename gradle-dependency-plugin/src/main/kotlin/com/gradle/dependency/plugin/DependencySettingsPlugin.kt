package com.gradle.dependency.plugin

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.model.ObjectFactory
import java.io.File
import javax.inject.Inject

/**
 * Settings plugin that installs the embedded version catalog and configures
 * pluginManagement/dependencyResolution repositories for consumers.
 */
abstract class DependencySettingsPlugin @Inject constructor(
    private val objects: ObjectFactory
) : Plugin<Settings> {
    override fun apply(settings: Settings) {
        // Configure pluginManagement repositories so plugin IDs can be resolved
        settings.pluginManagement.repositories.apply {
            // prefer mavenLocal for local development
            mavenLocal()
            gradlePluginPortal()
            mavenCentral()
        }

        // Configure dependency resolution repositories and apply embedded version catalog
        val resourcePath = "/catalogs/libs.versions.toml"
        val text = javaClass.getResourceAsStream(resourcePath)?.bufferedReader()?.readText()
            ?: throw GradleException("Could not find $resourcePath in plugin resources")

        val generated = File(settings.rootDir, "build/generated$resourcePath").apply {
            parentFile.mkdirs()
            writeText(text)
        }

        settings.dependencyResolutionManagement {
            @Suppress("UnstableApiUsage")
            repositories {
                mavenLocal()
                mavenCentral()
            }
            versionCatalogs {
                create("libs") {
                    // Use ObjectFactory to create proper FileCollection for catalog source
                    from(objects.fileCollection().from(generated))
                }
            }
        }

        // Helpful log for consumers
        println("📘 DependencySettingsPlugin applied: version catalog 'libs' installed from plugin resources")
    }
}
