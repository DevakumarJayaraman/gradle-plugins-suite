# DependencyPlugin - Step-by-Step Explanation

## Overview
`DependencyPlugin` is a Gradle plugin that automatically configures Java/Kotlin projects with:
- Repository management
- Java toolchain configuration
- Spring Boot integration (conditional)
- Version catalog management
- Testing framework setup

---

## Class Structure

```kotlin
class DependencyPlugin : Plugin<Project>
```
- Implements Gradle's `Plugin<Project>` interface
- Applied to Gradle projects to provide automatic dependency and build configuration

---

## Main Method: `apply(project: Project)`

### Step 1: Determine Pipeline Type
```kotlin
val pipelineType = (project.findProperty("pipelineType") as? String)?.uppercase() ?: "SERVICE"
```
- Reads the `pipelineType` property from gradle.properties or command line
- Converts to uppercase for consistency
- Defaults to "SERVICE" if not specified
- Two types supported:
  - **SERVICE**: Full application with Spring Boot
  - **LIB**: Library module without Spring Boot

### Step 2: Configure Repositories
```kotlin
configureRepositories(project)
```
- Calls helper method to set up Maven repositories
- Ensures consistent repository configuration across all projects

### Step 3: Apply Base Plugins
```kotlin
project.pluginManager.apply("java-library")
project.pluginManager.apply("org.jetbrains.kotlin.jvm")
```
- `java-library`: Adds Java compilation and library publishing capabilities
- `org.jetbrains.kotlin.jvm`: Enables Kotlin compilation for JVM

### Step 4: Conditional Spring Boot Plugin
```kotlin
if (pipelineType == "SERVICE") {
    project.pluginManager.apply("org.springframework.boot")
    project.logger.lifecycle("✅ SERVICE detected: Boot plugin applied")
} else {
    project.logger.lifecycle("📦 LIB detected: Boot plugin skipped")
}
```
- **SERVICE**: Applies Spring Boot plugin for creating executable applications
- **LIB**: Skips Spring Boot to create reusable libraries

### Step 5: Root Project Special Handling
```kotlin
if (project == project.rootProject) {
    project.gradle.settingsEvaluated { applyCatalogFromResource(this) }
    project.subprojects.forEach { configureRepositories(it) }
}
```
- Only runs for the root project
- `settingsEvaluated`: Hook that runs after settings.gradle.kts is evaluated
- Applies version catalog from plugin resources
- Ensures all subprojects get repository configuration

### Step 6: Post-Evaluation Configuration
```kotlin
project.afterEvaluate {
    // ... configuration
}
```
- Runs after the project build script is fully evaluated
- Ensures all project properties and extensions are available

#### 6a. Library-Specific Configuration
```kotlin
if (pipelineType == "LIB") {
    tasks.matching { it.name == "bootJar" }.configureEach { enabled = false }
    tasks.matching { it.name == "jar" }.configureEach { enabled = true }
}
```
- Disables `bootJar` task (creates executable JAR with embedded server)
- Enables standard `jar` task (creates library JAR)

#### 6b. Java Toolchain Configuration
```kotlin
extensions.configure<JavaPluginExtension> {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
```
- Sets Java version to 17
- Ensures consistent Java version across all modules
- Gradle will automatically download JDK 17 if not available

#### 6c. Test Configuration
```kotlin
tasks.withType<Test>().configureEach { useJUnitPlatform() }
```
- Configures all test tasks to use JUnit 5 (Jupiter)
- Required for running modern JUnit tests

---

## Helper Method: `configureRepositories(project: Project)`

### Step 1: Clear Existing Repositories
```kotlin
project.repositories.apply {
    clear()
    // ...
}
```
- Removes any previously configured repositories
- Ensures consistent repository order

### Step 2: Add Maven Repositories
```kotlin
mavenCentral()
maven("https://repo.spring.io/release")
maven {
    url = URI("https://artifactory.myorg.com/libs-release")
    isAllowInsecureProtocol = false
}
```
- **mavenCentral()**: Primary public Maven repository
- **repo.spring.io**: Spring Framework release repository
- **artifactory.myorg.com**: Custom organization artifact repository
  - `isAllowInsecureProtocol = false`: Enforces HTTPS

---

## Helper Method: `applyCatalogFromResource(settings: Settings)`

This method applies a version catalog from the plugin's embedded resources.

### Step 1: Load Resource File
```kotlin
val resourcePath = "/catalogs/libs.versions.toml"
val text = javaClass.getResourceAsStream(resourcePath)?.bufferedReader()?.readText()
    ?: throw GradleException("Could not find $resourcePath in plugin resources")
```
- Reads `libs.versions.toml` from plugin JAR's resources
- Throws exception if file not found
- This file contains centralized dependency versions

### Step 2: Write to Build Directory
```kotlin
val generated = File(settings.rootDir, "build/generated$resourcePath").apply {
    parentFile.mkdirs()
    writeText(text)
}
```
- Creates file in `build/generated/catalogs/libs.versions.toml`
- `parentFile.mkdirs()`: Creates parent directories if they don't exist
- Writes the content to the file

### Step 3: Configure Dependency Resolution
```kotlin
settings.dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories { mavenCentral() }
    
    versionCatalogs {
        val catalogFiles = settings.providers.provider { listOf(generated) }
        create("libs") {
            from(catalogFiles.get())
        }
    }
}
```

#### 3a. Repository Configuration
- Adds mavenCentral to settings-level repositories
- `@Suppress("UnstableApiUsage")`: Suppresses warning about incubating API

#### 3b. Version Catalog Creation
```kotlin
val catalogFiles = settings.providers.provider { listOf(generated) }
```
- **Providers API**: Lazy evaluation mechanism in Gradle
- Wraps the File in a Provider that returns a List
- This is required because `from()` expects a specific type

```kotlin
create("libs") {
    from(catalogFiles.get())
}
```
- Creates a version catalog named "libs"
- `from()`: Imports versions and dependencies from the TOML file
- `catalogFiles.get()`: Evaluates the provider to get the file list
- Now accessible in build scripts as `libs.versions.xyz` or `libs.dependencies.xyz`

### Step 4: Confirmation
```kotlin
println("📘 Version catalog applied from plugin resource.")
```
- Logs success message to console

---

## Key Gradle Concepts Used

### 1. **Plugin Interface**
- Implements `Plugin<Project>` to create reusable build logic

### 2. **Lifecycle Hooks**
- `settingsEvaluated`: Runs after settings phase
- `afterEvaluate`: Runs after project configuration phase

### 3. **Provider API**
- Lazy evaluation for configuration values
- Used here: `settings.providers.provider { listOf(generated) }`

### 4. **Version Catalogs**
- Centralized dependency management
- Defined in TOML format
- Shared across all modules

### 5. **Task Configuration**
- `tasks.matching`: Selects tasks by name
- `configureEach`: Lazily configures tasks

### 6. **Extension Configuration**
- `extensions.configure<JavaPluginExtension>`: Type-safe extension access
- Configures Java toolchain settings

---

## Why This Design?

### Benefits:
1. **Consistency**: All projects get the same repository and Java version
2. **Centralization**: Version catalog embedded in plugin
3. **Flexibility**: SERVICE vs LIB pipeline types
4. **Convention over Configuration**: Sensible defaults with minimal setup

### Trade-offs:
1. **Coupling**: Projects depend on plugin for basic configuration
2. **Customization**: Harder to override defaults
3. **Visibility**: Version catalog not visible in project source code

---

## Usage Example

In a project's `build.gradle.kts`:
```kotlin
plugins {
    id("com.example.gradle-dependency") version "1.0.0"
}

// Optional: specify pipeline type
// Default is SERVICE if not specified
```

In `gradle.properties`:
```properties
pipelineType=LIB  # or SERVICE
```

The plugin automatically:
- ✅ Configures repositories
- ✅ Applies Kotlin and Java plugins
- ✅ Sets Java 17 toolchain
- ✅ Configures JUnit 5
- ✅ Applies Spring Boot (if SERVICE)
- ✅ Imports version catalog

