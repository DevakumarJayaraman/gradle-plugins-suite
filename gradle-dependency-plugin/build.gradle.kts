plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    kotlin("jvm") version "2.0.21"
}

group = "com.gradle.dependency.plugin"
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
        create("gradleDependencyPlugin") {
            id = "com.gradle.dependency.plugin"
            implementationClass = "com.gradle.dependency.plugin.DependencyPlugin"
        }
        create("gradleDependencySettingsPlugin") {
            id = "com.gradle.dependency.settings"
            implementationClass = "com.gradle.dependency.plugin.DependencySettingsPlugin"
        }
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // Plugin dependencies - makes these plugins available when our plugin applies them
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.3.4")
}
