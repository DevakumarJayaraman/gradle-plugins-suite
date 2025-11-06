pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        // Provide Kotlin plugin version used by included builds / applied plugins
        id("org.jetbrains.kotlin.jvm") version "2.0.21"
        // Provide Spring Boot plugin version used by included builds / applied plugins
        id("org.springframework.boot") version "3.3.4"
    }
    // Ensure runtime resolution (pluginManager.apply) can find plugin modules
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.jetbrains.kotlin.jvm") {
                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
            }
            if (requested.id.id == "org.springframework.boot") {
                useModule("org.springframework.boot:spring-boot-gradle-plugin:3.3.4")
            }
        }
    }
    includeBuild("../gradle-dependency-plugin")
    includeBuild("../gradle-tooling-plugin")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle-dependency-plugin/src/main/resources/catalogs/libs.versions.toml"))
        }
    }
}

rootProject.name = "gradle-plugin-consumer"
