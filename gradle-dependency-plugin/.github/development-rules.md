# Development Rules for gradle-dependency-plugin

## Critical Rules (Must Follow)

### 1. Duplicate Resource Handling
**ALWAYS** include this in build.gradle.kts:
```kotlin
tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
```
**Why**: Plugin descriptor files can be duplicated during resource processing, causing build failures.

### 2. Import Requirements
Always include these imports in plugin files:
```kotlin
import org.gradle.api.*
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.*
import java.io.File
import java.net.URI  // Not java.net.URL
```

### 3. Settings API Usage
When working with Settings in version catalogs:
```kotlin
// ✅ CORRECT: Use Provider API
val catalogFiles = settings.providers.provider { listOf(generated) }
create("libs") {
    from(catalogFiles.get())
}

// ❌ WRONG: Direct file reference causes type inference errors
create("libs") {
    from(generated)
}

// ❌ WRONG: files() is not available in this context
create("libs") {
    from(files(generated))
}
```

### 4. Suppress Warnings Appropriately
```kotlin
// For unstable APIs
@Suppress("UnstableApiUsage")
repositories { mavenCentral() }

// For type inference warnings
@Suppress("UNCHECKED_CAST", "TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
create("libs") { ... }
```

### 5. Lazy Configuration
**ALWAYS** use lazy task configuration:
```kotlin
// ✅ CORRECT: Lazy configuration
tasks.withType<Test>().configureEach { useJUnitPlatform() }
tasks.matching { it.name == "bootJar" }.configureEach { enabled = false }

// ❌ WRONG: Eager configuration
tasks.withType<Test>().all { useJUnitPlatform() }
tasks.getByName("bootJar") { enabled = false }
```

### 6. URI vs URL
```kotlin
// ✅ CORRECT: Use java.net.URI
url = URI("https://example.com")

// ❌ WRONG: Don't use java.net.URL or uri() function
url = uri("https://example.com")
```

## Code Organization Rules

### 7. Method Order
Organize methods in this order:
1. `apply(project: Project)` - Main entry point
2. Private helper methods (alphabetically or by usage)
3. Keep related configuration together

### 8. Logging Standards
```kotlin
// For plugin lifecycle events
project.logger.lifecycle("🔧 Message")

// For success events
project.logger.lifecycle("✅ Success message")

// For informational events
project.logger.lifecycle("📦 Info message")

// For settings-level logging
println("📘 Message")  // settings.logger is not available
```

### 9. Error Messages
```kotlin
// ✅ GOOD: Descriptive error messages
throw GradleException("Could not find $resourcePath in plugin resources")

// ❌ BAD: Generic error messages
throw Exception("File not found")
```

## Build Script Rules

### 10. Plugin Declaration
In build.gradle.kts, always use:
```kotlin
plugins {
    `kotlin-dsl`           // Provides Gradle API and Kotlin DSL
    `java-gradle-plugin`   // Enables plugin development
    kotlin("jvm") version "2.0.21"
}
```

### 11. Plugin Registration
```kotlin
gradlePlugin {
    plugins {
        create("gradleDependencyPlugin") {
            id = "com.example.gradle-dependency"  // Must match property file name
            implementationClass = "com.example.deps.DependencyPlugin"
        }
    }
}
```

## Resource File Rules

### 12. Version Catalog Location
**MUST** be at: `src/main/resources/catalogs/libs.versions.toml`

### 13. Plugin Descriptor Location
**MUST** be at: `src/main/resources/META-INF/gradle-plugins/com.example.gradle-dependency.properties`

Content:
```properties
implementation-class=com.example.deps.DependencyPlugin
```

## Testing Rules

### 14. Local Testing Workflow
```bash
# 1. Build plugin
cd gradle-dependency-plugin
./gradlew clean build

# 2. Publish to local Maven
./gradlew publishToMavenLocal

# 3. Test in consumer
cd ../sample-service
./gradlew clean build --refresh-dependencies
```

### 15. Verify These After Changes
- [ ] No compilation errors
- [ ] No duplicate resource warnings
- [ ] Plugin builds successfully
- [ ] Version catalog is accessible in test project
- [ ] Correct plugins applied based on pipelineType
- [ ] Java toolchain version is correct

## API Usage Rules

### 16. Gradle Lifecycle Hooks
```kotlin
// For root project only actions
if (project == project.rootProject) {
    project.gradle.settingsEvaluated { 
        // Settings phase is complete
    }
}

// For actions after project configuration
project.afterEvaluate {
    // Project scripts have been evaluated
}
```

### 17. Extension Configuration
```kotlin
// ✅ CORRECT: Type-safe configuration
extensions.configure<JavaPluginExtension> {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

// ❌ WRONG: Unsafe casting
val java = extensions.getByName("java") as JavaPluginExtension
```

### 18. Repository Configuration
```kotlin
// ✅ CORRECT: Clear existing first
project.repositories.apply {
    clear()  // Ensures consistent order
    mavenCentral()
    maven("https://...")
}

// ❌ WRONG: Append without clearing
project.repositories.mavenCentral()
```

## Performance Rules

### 19. Provider API for Late Binding
```kotlin
// ✅ GOOD: Lazy evaluation
val catalogFiles = settings.providers.provider { listOf(generated) }

// ❌ BAD: Eager evaluation
val catalogFiles = listOf(generated)
```

### 20. File Generation
```kotlin
// ✅ GOOD: Generate in build directory
File(settings.rootDir, "build/generated$resourcePath")

// ❌ BAD: Generate in source directory
File(settings.rootDir, "src/main/resources$resourcePath")
```

## Documentation Rules

### 21. Inline Documentation
Add comments for:
- Complex logic
- Non-obvious API usage
- Why a specific approach was chosen
- Workarounds for Gradle API limitations

### 22. Public API Documentation
Document these for consumers:
- Required properties (pipelineType)
- Available pipeline types
- Applied plugins
- Configured extensions
- Version catalog usage

## Version Management Rules

### 23. Plugin Version Updates
When updating the plugin:
1. Update version in build.gradle.kts
2. Test locally first
3. Document breaking changes
4. Update sample projects
5. Publish to artifact repository

### 24. Dependency Version Updates
When updating versions in libs.versions.toml:
1. Test compatibility locally
2. Check for breaking changes
3. Update documentation
4. Communicate to consuming teams

## Security Rules

### 25. Repository Security
```kotlin
// ✅ ALWAYS enforce HTTPS
maven {
    url = URI("https://...")
    isAllowInsecureProtocol = false
}

// ❌ NEVER allow insecure protocols
isAllowInsecureProtocol = true
```

### 26. Credentials Management
```kotlin
// ✅ GOOD: Use project properties
credentials {
    username = project.findProperty("repoUsername") as String?
    password = project.findProperty("repoPassword") as String?
}

// ❌ BAD: Hardcoded credentials
credentials {
    username = "user"
    password = "pass"
}
```

## Compatibility Rules

### 27. Gradle Version Compatibility
- Target Gradle 8.x
- Use stable APIs when possible
- Suppress warnings for @Incubating APIs
- Test with minimum supported Gradle version

### 28. Kotlin Version Compatibility
- Use Kotlin 2.0.21 or later
- Avoid experimental features
- Use stable stdlib functions

## Emergency Procedures

### If Build Breaks:

1. **Check for duplicate resources**
   - Verify `duplicatesStrategy` is set
   
2. **Check imports**
   - Ensure all required imports are present
   - Use `java.net.URI`, not `uri()`
   
3. **Check Provider API usage**
   - Version catalogs need Provider-wrapped files
   
4. **Rebuild from clean state**
   ```bash
   ./gradlew clean build --refresh-dependencies
   ```

5. **Check IDE sync**
   - Reimport Gradle project
   - Invalidate caches if needed

## When to Update These Rules

Add new rules when:
- A pattern causes repeated issues
- A new Gradle API is adopted
- A common mistake is discovered
- A security concern is identified
- A performance optimization is found

---

**Last Updated**: November 6, 2025
**Maintained By**: Development Team
**Review Frequency**: After each major Gradle version update

