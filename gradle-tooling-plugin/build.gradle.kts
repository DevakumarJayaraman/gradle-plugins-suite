plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    kotlin("jvm") version "2.0.21"
}

group = "com.gradle.tooling.plugin"
version = "1.0.2"

kotlin {
    jvmToolchain(17)
}


tasks.withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
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
