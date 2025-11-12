plugins {
    // Convention plugins - settings plugin loads the artifact, so no version needed here
    id("com.gradle.dependency.plugin")
    id("com.gradle.tooling.plugin") version "1.0.2"
}


dependencies {
    implementation(libs.spring.boot.starter.web){
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
    implementation(libs.spring.boot.starter.jetty)

    implementation(libs.gradle.tooling.plugin)

    constraints {
        // use the fully-qualified module coordinate
        implementation("org.springframework.boot:spring-boot-starter-web:3.3.4") {
            because("Force Spring Boot web - override plugin catalog")
        }
    }

    testImplementation(libs.spring.boot.starter.test)
}
