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
        create("gradleDependencyPlugin") {
            id = "com.gradle.dependency.plugin"
            implementationClass = "com.gradle.dependency.plugin.DependencyPlugin"
        }
    }
}
repositories { mavenCentral() }
