plugins {
    // Convention plugins - settings plugin loads the artifact, so no version needed here
    id("com.gradle.dependency.plugin")
    id("com.gradle.tooling.plugin") version "1.0.1"
}


dependencies {
    implementation(libs.spring.boot.starter.web)
    testImplementation(libs.spring.boot.starter.test)
}
