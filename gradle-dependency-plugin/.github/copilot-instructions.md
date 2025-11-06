# GitHub Copilot Instructions for gradle-dependency-plugin

## Project Overview
This is a **Gradle Convention Plugin** that provides standardized dependency management, repository configuration, and build setup for Java/Kotlin projects.

## Core Principles

### 1. Convention Over Configuration
- Provide sensible defaults that work for 90% of use cases
- Allow minimal configuration in consuming projects
- Support two pipeline types: SERVICE (applications) and LIB (libraries)

### 2. Centralized Version Management
- Version catalog (`libs.versions.toml`) is embedded in the plugin JAR
- Consuming projects automatically inherit versions
- Updates happen by updating the plugin version

### 3. Consistent Build Configuration
- All projects use the same repositories
- All projects use Java 17 toolchain
- All projects use JUnit 5 for testing

## Code Style Guidelines

### Kotlin Conventions
```kotlin
// ✅ Good: Use property access syntax
project.logger.lifecycle("Message")

// ❌ Bad: Avoid getter methods
project.getLogger().lifecycle("Message")

// ✅ Good: Use extension functions
extensions.configure<JavaPluginExtension> { }

// ✅ Good: Use apply scope functions
project.repositories.apply {
    clear()
    mavenCentral()
}
```

### Gradle API Usage
```kotlin
// ✅ Good: Use lazy task configuration
tasks.withType<Test>().configureEach { }

// ❌ Bad: Avoid eager task creation
tasks.withType<Test>().all { }

// ✅ Good: Use Provider API for lazy values
val catalogFiles = settings.providers.provider { listOf(generated) }

// ✅ Good: Suppress warnings appropriately
@Suppress("UnstableApiUsage")
repositories { mavenCentral() }
```

### Error Handling
```kotlin
// ✅ Good: Throw GradleException for plugin errors
?: throw GradleException("Could not find $resourcePath in plugin resources")

// ✅ Good: Use safe calls with Elvis operator
val pipelineType = (project.findProperty("pipelineType") as? String)?.uppercase() ?: "SERVICE"
```

## File Organization

```
gradle-dependency-plugin/
├── src/main/
│   ├── kotlin/com/example/deps/
│   │   └── DependencyPlugin.kt          # Main plugin implementation
│   └── resources/
│       ├── catalogs/
│       │   └── libs.versions.toml       # Version catalog
│       └── META-INF/gradle-plugins/
│           └── com.example.gradle-dependency.properties
├── build.gradle.kts                      # Plugin build configuration
└── .github/
    └── copilot-instructions.md          # This file
```

## Common Patterns

### Adding a New Repository
```kotlin
// In configureRepositories method
maven {
    url = URI("https://repo.example.com/releases")
    isAllowInsecureProtocol = false
    credentials {
        username = project.findProperty("repoUsername") as String?
        password = project.findProperty("repoPassword") as String?
    }
}
```

### Adding a New Plugin
```kotlin
// In apply method
project.pluginManager.apply("plugin-id")
project.logger.lifecycle("✅ Plugin applied")
```

### Configuring a New Extension
```kotlin
// In afterEvaluate block
extensions.configure<ExtensionType> {
    // Configuration
}
```

### Adding a New Pipeline Type
```kotlin
// In apply method
when (pipelineType) {
    "SERVICE" -> {
        project.pluginManager.apply("org.springframework.boot")
    }
    "LIB" -> {
        // Skip Spring Boot
    }
    "ANDROID" -> {
        project.pluginManager.apply("com.android.library")
    }
    else -> throw GradleException("Unknown pipelineType: $pipelineType")
}
```

## Testing Strategy

### Manual Testing
```bash
# Build the plugin
./gradlew clean build

# Publish to local Maven
./gradlew publishToMavenLocal

# Test in a consumer project
cd ../sample-service
./gradlew clean build --refresh-dependencies
```

### Verification Checklist
- [ ] Plugin builds without errors
- [ ] No duplicate resource warnings
- [ ] Version catalog is accessible in consuming projects
- [ ] Correct plugins applied based on pipelineType
- [ ] Java toolchain set to correct version
- [ ] Tests run with JUnit 5

## Common Issues and Solutions

### Issue: "Entry META-INF/gradle-plugins/... is a duplicate"
**Solution**: Add duplicate strategy to build.gradle.kts
```kotlin
tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
```

### Issue: "Unresolved reference: JavaLanguageVersion"
**Solution**: Ensure correct imports
```kotlin
import org.gradle.jvm.toolchain.JavaLanguageVersion
```

### Issue: "Cannot infer type for type parameter 'T'"
**Solution**: Use Provider API properly
```kotlin
val catalogFiles = settings.providers.provider { listOf(generated) }
create("libs") {
    from(catalogFiles.get())
}
```

### Issue: Version catalog not found in consumer projects
**Solution**: Ensure the TOML file is in src/main/resources/catalogs/

## Build Configuration

### Required in build.gradle.kts
```kotlin
plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    kotlin("jvm") version "2.0.21"
}

// Essential for avoiding duplicate resources
tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

gradlePlugin {
    plugins {
        create("gradleDependencyPlugin") {
            id = "com.example.gradle-dependency"
            implementationClass = "com.example.deps.DependencyPlugin"
        }
    }
}
```

## Dependencies

### Plugin Dependencies
- Gradle API (provided by `kotlin-dsl`)
- Kotlin stdlib (provided by `kotlin-dsl`)

### Consumer Project Dependencies
Projects using this plugin automatically get:
- `java-library` plugin
- `org.jetbrains.kotlin.jvm` plugin
- `org.springframework.boot` plugin (if pipelineType=SERVICE)

## Version Catalog Format

The `libs.versions.toml` should follow this structure:
```toml
[versions]
spring-boot = "3.2.0"
kotlin = "2.0.21"

[libraries]
spring-boot-starter-web = { module = "org.springframework.boot:spring-boot-starter-web", version.ref = "spring-boot" }

[plugins]
spring-boot = { id = "org.springframework.boot", version.ref = "spring-boot" }
```

## Future Enhancements

Consider adding:
- [ ] Support for custom repository credentials from environment variables
- [ ] Configurable Java version (not hardcoded to 17)
- [ ] Additional pipeline types (ANDROID, KOTLIN_MULTIPLATFORM)
- [ ] Automatic dependency vulnerability scanning
- [ ] Code coverage configuration
- [ ] Publishing configuration for libraries

## Publishing

When publishing updates:
1. Update version in build.gradle.kts
2. Update version catalog if needed
3. Build and test locally
4. Publish to artifact repository
5. Update consuming projects to new version

## Support

For issues or questions:
- Check this file for common patterns
- Review DependencyPlugin-Explanation.md for detailed explanations
- Check Gradle documentation for API changes
- Test changes locally before publishing

