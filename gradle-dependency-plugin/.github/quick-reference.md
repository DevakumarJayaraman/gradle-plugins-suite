# Quick Reference Guide - gradle-dependency-plugin

## 🚀 Quick Start

### Apply the Plugin
```kotlin
// build.gradle.kts
plugins {
    id("com.example.gradle-dependency") version "1.0.0"
}
```

### Set Pipeline Type
```properties
# gradle.properties
pipelineType=SERVICE  # or LIB
```

---

## 📋 What This Plugin Does

| Feature | Description |
|---------|-------------|
| **Repositories** | Automatically configures Maven Central, Spring repos, and custom repos |
| **Java Toolchain** | Sets Java 17 as the compilation and runtime version |
| **Kotlin Support** | Applies Kotlin JVM plugin |
| **Spring Boot** | Conditionally applies based on pipelineType |
| **Version Catalog** | Provides centralized dependency versions |
| **Testing** | Configures JUnit 5 Platform |

---

## 🔧 Pipeline Types

### SERVICE
- Full Spring Boot application
- Creates executable JAR with `bootJar` task
- Includes embedded web server
- Use for: Microservices, web applications, batch jobs

### LIB
- Library/module without Spring Boot
- Creates standard JAR with `jar` task
- No embedded server
- Use for: Shared libraries, common modules, utilities

---

## 📦 Automatic Configuration

### Plugins Applied
```kotlin
✅ java-library
✅ org.jetbrains.kotlin.jvm
✅ org.springframework.boot (if SERVICE)
```

### Repositories Added
```kotlin
✅ Maven Central
✅ https://repo.spring.io/release
✅ https://artifactory.myorg.com/libs-release
```

### Java Configuration
```kotlin
✅ Java 17 toolchain
✅ JUnit 5 platform
```

---

## 💡 Common Patterns

### Access Version Catalog
```kotlin
dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.kotlin.stdlib)
    testImplementation(libs.junit.jupiter)
}
```

### Override Repository (if needed)
```kotlin
repositories {
    // Plugin repos are already configured
    // Add additional repos here if needed
    maven("https://custom-repo.example.com")
}
```

### Add Dependencies
```kotlin
dependencies {
    // Use version catalog
    implementation(libs.some.library)
    
    // Or specify version directly
    implementation("com.example:library:1.0.0")
}
```

---

## 🐛 Troubleshooting

### Version Catalog Not Found
**Solution**: Make sure plugin is applied and Gradle is synced
```bash
./gradlew clean build --refresh-dependencies
```

### Wrong Java Version
**Problem**: Using Java 11 but project expects 17
**Solution**: The plugin automatically configures Java 17 toolchain. Gradle will download it if needed.

### Spring Boot Plugin Not Applied
**Problem**: SERVICE type project not creating bootJar
**Solution**: Check gradle.properties has `pipelineType=SERVICE`

### Build Fails with Duplicate Resources
**Problem**: Should be fixed in the plugin
**Solution**: Update to latest plugin version or check development-rules.md

---

## 📝 Build Tasks

### Common Tasks
```bash
# Build project
./gradlew build

# Run application (SERVICE only)
./gradlew bootRun

# Run tests
./gradlew test

# Create JAR (LIB)
./gradlew jar

# Create executable JAR (SERVICE)
./gradlew bootJar

# Clean build
./gradlew clean build
```

---

## 🔍 Verification Commands

### Check Applied Plugins
```bash
./gradlew plugins
```

### Check Dependencies
```bash
./gradlew dependencies
```

### Check Tasks
```bash
./gradlew tasks --all
```

### Check Java Version
```bash
./gradlew -q javaToolchains
```

---

## 📚 File Structure

After applying the plugin, your project should have:

```
my-project/
├── gradle.properties          # Set pipelineType here
├── build.gradle.kts           # Apply plugin here
├── settings.gradle.kts        # Standard settings
└── src/
    ├── main/
    │   ├── kotlin/           # Your source code
    │   └── resources/        # Your resources
    └── test/
        ├── kotlin/           # Your tests
        └── resources/        # Test resources
```

**Note**: Version catalog is embedded in the plugin, not in your project.

---

## ⚡ Performance Tips

1. **Use Build Cache**
   ```bash
   ./gradlew build --build-cache
   ```

2. **Parallel Builds**
   ```properties
   # gradle.properties
   org.gradle.parallel=true
   ```

3. **Configuration Cache**
   ```bash
   ./gradlew build --configuration-cache
   ```

---

## 🆘 Getting Help

### Check Documentation
1. Read `DependencyPlugin-Explanation.md` for detailed explanation
2. Read `development-rules.md` for development guidelines
3. Read `copilot-instructions.md` for code patterns

### Debug Build
```bash
# See all logging
./gradlew build --info

# See stack traces
./gradlew build --stacktrace

# Debug mode
./gradlew build --debug
```

### Common Error Messages

| Error | Cause | Solution |
|-------|-------|----------|
| "Unresolved reference: libs" | Version catalog not loaded | Sync Gradle, check plugin applied |
| "Could not find com.example.gradle-dependency" | Plugin not published | Check version, run publishToMavenLocal |
| "bootJar task not found" | LIB pipeline type | Change to SERVICE or use jar task |
| "Execution failed for task ':jar'" | SERVICE pipeline | Use bootJar task instead |

---

## 🔄 Update Guide

### Updating Plugin Version
```kotlin
// build.gradle.kts
plugins {
    id("com.example.gradle-dependency") version "1.1.0" // Update here
}
```

Then:
```bash
./gradlew clean build --refresh-dependencies
```

### Checking What Changed
1. Review plugin release notes
2. Test locally before deploying
3. Check for breaking changes
4. Update consuming projects

---

## 🎯 Best Practices

### DO ✅
- Use the version catalog for dependencies
- Set pipelineType explicitly
- Keep plugin version up to date
- Use standard project structure
- Run tests before committing

### DON'T ❌
- Don't manually apply java or kotlin plugins
- Don't hardcode dependency versions
- Don't modify generated files in build/
- Don't commit gradle.properties with credentials
- Don't skip tests

---

## 🔗 Quick Links

- [Gradle Documentation](https://docs.gradle.org)
- [Kotlin DSL Guide](https://docs.gradle.org/current/userguide/kotlin_dsl.html)
- [Spring Boot Gradle Plugin](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

---

## 📞 Support Contacts

| Issue Type | Contact |
|------------|---------|
| Plugin bugs | Development team |
| Build failures | Check documentation first |
| Feature requests | Submit to team backlog |
| Security issues | Report immediately |

---

**Last Updated**: November 6, 2025  
**Plugin Version**: 1.0.0  
**Minimum Gradle Version**: 8.0+  
**Java Version**: 17

