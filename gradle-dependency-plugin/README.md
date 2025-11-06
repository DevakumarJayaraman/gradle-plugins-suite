# Gradle Dependency Plugin

A Gradle convention plugin that provides standardized dependency management, repository configuration, and build setup for Java/Kotlin projects.

## Features

- 🔧 **Automatic Plugin Configuration**: Applies Java, Kotlin, and Spring Boot plugins based on project type
- 📦 **Centralized Repository Management**: Configures Maven Central, Spring repos, and custom repositories
- 📚 **Version Catalog Integration**: Embedded version catalog for consistent dependency versions
- ⚙️ **Java Toolchain**: Automatically configures Java 17 toolchain
- 🧪 **Testing Setup**: Configures JUnit 5 Platform
- 🚀 **Pipeline Type Support**: SERVICE (applications) vs LIB (libraries)

## Quick Start

### 1. Apply the Plugin

In your `build.gradle.kts`:
```kotlin
plugins {
    id("com.example.gradle-dependency") version "1.0.0"
}
```

### 2. Set Pipeline Type

In your `gradle.properties`:
```properties
pipelineType=SERVICE  # or LIB
```

That's it! The plugin handles the rest.

## Pipeline Types

### SERVICE
Full-featured application configuration:
- ✅ Spring Boot plugin applied
- ✅ Creates executable JAR (`bootJar` task)
- ✅ Includes embedded server
- 🎯 Use for: Microservices, web apps, batch jobs

### LIB
Library/module configuration:
- ✅ Spring Boot plugin skipped
- ✅ Creates standard JAR (`jar` task)
- ✅ No embedded server
- 🎯 Use for: Shared libraries, common modules

## What Gets Configured

### Plugins
```kotlin
✓ java-library
✓ org.jetbrains.kotlin.jvm
✓ org.springframework.boot (SERVICE only)
```

### Repositories
```kotlin
✓ Maven Central
✓ Spring Release Repository
✓ Custom Artifactory Repository
```

### Java Configuration
```kotlin
✓ Java 17 Toolchain
✓ JUnit 5 Platform
```

### Version Catalog
Access dependencies via the embedded catalog:
```kotlin
dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.kotlin.stdlib)
    testImplementation(libs.junit.jupiter)
}
```

## Usage Examples

### Basic Service Application
```kotlin
// build.gradle.kts
plugins {
    id("com.example.gradle-dependency") version "1.0.0"
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    testImplementation(libs.spring.boot.starter.test)
}
```

### Library Module
```kotlin
// build.gradle.kts
plugins {
    id("com.example.gradle-dependency") version "1.0.0"
}

// gradle.properties
pipelineType=LIB

dependencies {
    implementation(libs.kotlin.stdlib)
    testImplementation(libs.junit.jupiter)
}
```

## Build Tasks

```bash
# Build project
./gradlew build

# Run application (SERVICE only)
./gradlew bootRun

# Run tests
./gradlew test

# Create JAR
./gradlew jar          # For LIB
./gradlew bootJar      # For SERVICE
```

## Configuration Details

### Automatic Repository Configuration
The plugin automatically configures these repositories for all projects:

1. **Maven Central** - Primary public repository
2. **Spring Release** - Spring Framework releases
3. **Custom Artifactory** - Organization-specific artifacts

All repositories enforce HTTPS for security.

### Java Toolchain
The plugin sets Java 17 as the compilation target. Gradle will automatically download JDK 17 if it's not already available on your system.

### Version Catalog
The version catalog is embedded in the plugin JAR and automatically made available to all projects. This ensures consistent dependency versions across your organization.

## Multi-Module Projects

For multi-module projects, apply the plugin to each module:

```kotlin
// root build.gradle.kts
plugins {
    id("com.example.gradle-dependency") version "1.0.0"
}

// service-module/build.gradle.kts
plugins {
    id("com.example.gradle-dependency")
}
// gradle.properties: pipelineType=SERVICE

// lib-module/build.gradle.kts
plugins {
    id("com.example.gradle-dependency")
}
// gradle.properties: pipelineType=LIB
```

## Troubleshooting

### Version Catalog Not Found
**Solution**: Ensure the plugin is applied and sync Gradle
```bash
./gradlew clean build --refresh-dependencies
```

### Wrong Java Version
**Solution**: The plugin automatically handles Java 17. Run:
```bash
./gradlew -q javaToolchains
```

### Spring Boot Plugin Issues
**Check**: Verify `pipelineType=SERVICE` in gradle.properties
```bash
./gradlew properties | grep pipelineType
```

### Build Failures
**Debug**: Run with stack traces
```bash
./gradlew build --stacktrace --info
```

## Requirements

- **Gradle**: 8.0 or higher
- **Java**: 17 (automatically configured)
- **Kotlin**: 2.0.21 (provided by plugin)

## Documentation

- 📖 [Detailed Explanation](/.github/DependencyPlugin-Explanation.md) - Step-by-step code walkthrough
- 🔧 [Development Rules](/.github/development-rules.md) - Guidelines for modifying the plugin
- 📝 [Quick Reference](/.github/quick-reference.md) - Common patterns and commands
- 💡 [Copilot Instructions](/.github/copilot-instructions.md) - AI-assisted development guide

## Development

### Building the Plugin
```bash
./gradlew clean build
```

### Publishing Locally
```bash
./gradlew publishToMavenLocal
```

### Testing Changes
```bash
# In consumer project
./gradlew clean build --refresh-dependencies
```

## Project Structure

```
gradle-dependency-plugin/
├── .github/
│   ├── copilot-instructions.md
│   ├── development-rules.md
│   └── quick-reference.md
├── src/main/
│   ├── kotlin/com/example/deps/
│   │   └── DependencyPlugin.kt
│   └── resources/
│       ├── catalogs/
│       │   └── libs.versions.toml
│       └── META-INF/gradle-plugins/
│           └── com.example.gradle-dependency.properties
├── build.gradle.kts
└── README.md
```

## Contributing

When contributing to this plugin:

1. Read the [Development Rules](/.github/development-rules.md)
2. Follow the established patterns in [Copilot Instructions](/.github/copilot-instructions.md)
3. Test locally before submitting
4. Update documentation as needed

## Key Design Principles

1. **Convention over Configuration** - Sensible defaults that work for most cases
2. **Zero Configuration** - Works out of the box with minimal setup
3. **Centralized Management** - Version catalog embedded in plugin
4. **Type Safety** - Leverages Kotlin DSL for type-safe configuration
5. **Performance** - Uses lazy configuration APIs

## Benefits

### For Developers
- ✅ Less boilerplate in build scripts
- ✅ Consistent configuration across projects
- ✅ Automatic dependency updates via plugin version
- ✅ No need to manage version catalogs manually

### For Teams
- ✅ Standardized build configuration
- ✅ Easier onboarding for new projects
- ✅ Centralized dependency management
- ✅ Reduced configuration drift

### For Organizations
- ✅ Enforced repository security
- ✅ Consistent toolchain versions
- ✅ Easier compliance and auditing
- ✅ Simplified dependency updates

## Version History

### 1.0.0 (Current)
- Initial release
- Support for SERVICE and LIB pipeline types
- Embedded version catalog
- Java 17 toolchain
- JUnit 5 configuration

## License

[Your License Here]

## Support

For issues, questions, or feature requests:
- Check the [Quick Reference](/.github/quick-reference.md)
- Review the [Detailed Explanation](/.github/DependencyPlugin-Explanation.md)
- Contact the development team

---

**Built with ❤️ using Gradle and Kotlin**

