pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

plugins {
    // Use the settings plugin published to mavenLocal; version must match the published plugin
    id("com.gradle.dependency.settings") version "1.0.2"
}

// The settings plugin will configure dependencyResolutionManagement (version catalog and repos).

rootProject.name = "gradle-plugin-consumer"
