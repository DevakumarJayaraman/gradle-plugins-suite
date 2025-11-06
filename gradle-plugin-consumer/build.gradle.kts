plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.springframework.boot")
    id("com.gradle.dependency.plugin")
    id("com.gradle.tooling.plugin")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    testImplementation(libs.spring.boot.starter.test)
}
