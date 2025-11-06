
plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    kotlin("jvm") version "2.0.21"
}

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

gradlePlugin {
    plugins {
        create("gradleToolingPlugin") {
            id = "com.gradle.tooling.plugin"
            implementationClass = "com.gradle.tooling.plugin.ToolingPlugin"
        }
    }
}

repositories {
    mavenCentral()
}

