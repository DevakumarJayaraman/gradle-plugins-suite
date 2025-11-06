# Gradle Tooling Plugin

A Gradle plugin that provides useful development and debugging tasks for your projects.

## Features

- 🔧 **Development Tools**: Helpful tasks for debugging and project inspection
- 📊 **Environment Information**: Display system and project details
- 📦 **Dependency Analysis**: View project dependencies
- 📋 **Task Management**: List and explore available tasks
- ⚙️ **Property Inspection**: View all project properties
- 🧹 **Deep Cleaning**: Aggressive cleanup for troubleshooting

## Quick Start

### Apply the Plugin

In your `build.gradle.kts`:
```kotlin
plugins {
    id("com.example.gradle-tooling") version "1.0.0"
}
```

### View Available Tasks

```bash
./gradlew tasks --group tooling
```

That's it! The plugin adds 6 useful tasks to your project.

## Available Tasks

### 1. `helloTooling`
**Purpose**: Simple greeting task to verify plugin installation

**Usage**:
```bash
./gradlew helloTooling
```

**Output**:
```
👋 Hello from gradle-tooling-plugin!
✅ HelloTooling task completed successfully
```

---

### 2. `envSummary`
**Purpose**: Display comprehensive environment and project information

**Usage**:
```bash
./gradlew envSummary
```

**What It Shows**:
- ✅ Project information (name, group, version, paths)
- ✅ Gradle version and home directory
- ✅ Java version, vendor, and home
- ✅ Operating system details

**Example Output**:
```
📊 Environment Summary
==================================================
Project: my-service
Group: com.example
Version: 1.0.0
Path: :my-service
Build Dir: /path/to/build

Gradle Version: 8.5
Gradle Home: /path/to/gradle

Java Version: 17.0.8
Java Vendor: Eclipse Adoptium
Java Home: /path/to/jdk

OS Name: Mac OS X
OS Version: 14.0
OS Arch: aarch64
==================================================
✅ Environment summary completed
```

---

### 3. `dependencyReport`
**Purpose**: Display all project dependencies in a readable format

**Usage**:
```bash
./gradlew dependencyReport
```

**What It Shows**:
- ✅ Dependencies grouped by configuration
- ✅ Resolved artifact versions
- ✅ Module version IDs

**Example Output**:
```
📦 Dependency Report for my-service
==================================================

Configuration: compileClasspath
--------------------------------------------------
  • org.springframework.boot:spring-boot-starter-web:3.2.0
  • org.jetbrains.kotlin:kotlin-stdlib:2.0.21

Configuration: runtimeClasspath
--------------------------------------------------
  • org.springframework.boot:spring-boot-starter-web:3.2.0
  • com.fasterxml.jackson.core:jackson-databind:2.15.3

==================================================
✅ Dependency report completed
```

---

### 4. `taskList`
**Purpose**: List all available tasks grouped by category

**Usage**:
```bash
./gradlew taskList
```

**What It Shows**:
- ✅ All tasks grouped by category
- ✅ Task descriptions
- ✅ Total task count

**Example Output**:
```
📋 Task List for my-service
==================================================

BUILD tasks
--------------------------------------------------
  assemble - Assembles the outputs of this project
  build - Assembles and tests this project
  clean - Deletes the build directory

TOOLING tasks
--------------------------------------------------
  envSummary - Prints environment and project summary
  helloTooling - Prints a greeting message

==================================================
Total tasks: 47
✅ Task list completed
```

---

### 5. `projectProperties`
**Purpose**: Display all project properties and applied plugins

**Usage**:
```bash
./gradlew projectProperties
```

**What It Shows**:
- ✅ Standard properties (name, group, version, paths)
- ✅ Custom properties from gradle.properties
- ✅ Applied plugins

**Example Output**:
```
⚙️  Project Properties for my-service
==================================================

Standard Properties:
--------------------------------------------------
  name: my-service
  group: com.example
  version: 1.0.0
  path: :my-service
  projectDir: /path/to/project
  buildDir: /path/to/build
  rootDir: /path/to/root

Custom Properties:
--------------------------------------------------
  pipelineType: SERVICE
  springBootVersion: 3.2.0

Applied Plugins:
--------------------------------------------------
  • JavaPlugin
  • KotlinPlugin
  • SpringBootPlugin
  • ToolingPlugin

==================================================
✅ Properties display completed
```

---

### 6. `deepClean`
**Purpose**: Perform aggressive cleanup of build artifacts and caches

**Usage**:
```bash
./gradlew deepClean
```

**What It Does**:
- ✅ Deletes the entire `build/` directory
- ✅ Deletes the `.gradle/` directory in the project
- ✅ Shows size of deleted files

**Example Output**:
```
🧹 Deep Clean for my-service
==================================================
Deleting build directory: /path/to/build
  ✅ Deleted (45 MB)
Deleting .gradle directory: /path/to/.gradle
  ✅ Deleted (12 MB)

==================================================
Deleted 2 directories
Freed 57 MB
✅ Deep clean completed
```

**⚠️ Warning**: This task is more aggressive than standard `clean`. Use when:
- Build cache is corrupted
- Having unexplainable build issues
- Need to verify a completely clean build

---

## Common Use Cases

### Debugging Build Issues
```bash
# 1. Check environment
./gradlew envSummary

# 2. Check properties
./gradlew projectProperties

# 3. Deep clean if needed
./gradlew deepClean

# 4. Rebuild
./gradlew clean build
```

### Understanding Dependencies
```bash
# View all dependencies
./gradlew dependencyReport

# Or use Gradle's built-in (more detailed)
./gradlew dependencies
```

### Exploring Tasks
```bash
# Quick overview with tooling plugin
./gradlew taskList

# Or use Gradle's built-in (by group)
./gradlew tasks --all
```

### Verifying Plugin Installation
```bash
# Simple check
./gradlew helloTooling

# View all tooling tasks
./gradlew tasks --group tooling
```

---

## Integration with Other Plugins

This plugin works seamlessly with:
- ✅ gradle-dependency-plugin
- ✅ Spring Boot Gradle Plugin
- ✅ Kotlin Gradle Plugin
- ✅ Any other Gradle plugins

Example combined usage:
```kotlin
plugins {
    id("com.example.gradle-dependency") version "1.0.0"
    id("com.example.gradle-tooling") version "1.0.0"
}
```

---

## Task Execution Order

Tasks can be combined:
```bash
# Clean then check environment
./gradlew deepClean envSummary

# View properties and dependencies
./gradlew projectProperties dependencyReport

# Full diagnostic
./gradlew envSummary projectProperties dependencyReport taskList
```

---

## Requirements

- **Gradle**: 8.0 or higher
- **Kotlin**: 2.0.21 (provided by plugin)
- **Java**: Any version (no specific requirement)

---

## Project Structure

```
gradle-tooling-plugin/
├── build.gradle.kts
├── src/main/
│   ├── kotlin/com/example/tooling/
│   │   ├── ToolingPlugin.kt
│   │   └── tasks/
│   │       ├── HelloToolingTask.kt
│   │       ├── EnvSummaryTask.kt
│   │       ├── DependencyReportTask.kt
│   │       ├── TaskListTask.kt
│   │       ├── ProjectPropertiesTask.kt
│   │       └── DeepCleanTask.kt
│   └── resources/
│       └── META-INF/gradle-plugins/
│           └── com.example.gradle-tooling.properties
└── README.md
```

---

## Development

### Building the Plugin
```bash
cd gradle-tooling-plugin
./gradlew clean build
```

### Publishing Locally
```bash
./gradlew publishToMavenLocal
```

### Testing in a Project
```kotlin
// In consumer project's settings.gradle.kts
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

// In build.gradle.kts
plugins {
    id("com.example.gradle-tooling") version "1.0.0"
}
```

Then run:
```bash
./gradlew tasks --group tooling
./gradlew envSummary
```

---

## Extending the Plugin

To add new tasks:

1. **Create Task Class**:
   ```kotlin
   // src/main/kotlin/com/example/tooling/tasks/MyTask.kt
   package com.example.tooling.tasks
   
   import org.gradle.api.DefaultTask
   import org.gradle.api.tasks.TaskAction
   
   abstract class MyTask : DefaultTask() {
       init {
           group = "tooling"
           description = "Does something useful"
       }
       
       @TaskAction
       fun execute() {
           println("Hello from MyTask!")
       }
   }
   ```

2. **Register in Plugin**:
   ```kotlin
   // In ToolingPlugin.kt
   project.tasks.register<MyTask>("myTask")
   ```

3. **Rebuild and Test**:
   ```bash
   ./gradlew clean build publishToMavenLocal
   ```

---

## Best Practices

### DO ✅
- Use tooling tasks for debugging and exploration
- Run `envSummary` when reporting issues
- Use `deepClean` when having unexplainable build problems
- Combine tasks for comprehensive diagnostics
- Keep tasks focused on single responsibilities

### DON'T ❌
- Don't use `deepClean` in CI/CD pipelines (too aggressive)
- Don't rely on these tasks for production builds
- Don't modify task output format (breaks scripts)
- Don't run tasks that resolve configurations during configuration phase

---

## Troubleshooting

### Task Not Found
**Problem**: `Task 'envSummary' not found`

**Solution**: 
1. Verify plugin is applied in build.gradle.kts
2. Sync Gradle: `./gradlew --refresh-dependencies`
3. Check plugin version is correct

### Dependency Report Shows Nothing
**Problem**: `dependencyReport` shows no dependencies

**Solution**: This is normal if:
- Project has no dependencies
- Configurations are not resolvable
- Running on a parent project with no direct dependencies

### Deep Clean Fails
**Problem**: `deepClean` can't delete some files

**Solution**:
- Close IDE that might have files locked
- Stop Gradle daemon: `./gradlew --stop`
- Run with elevated permissions if needed

---

## Contributing

To contribute new tasks:
1. Create task class in `tasks/` package
2. Extend `DefaultTask`
3. Add `@TaskAction` method
4. Set `group = "tooling"`
5. Provide clear description
6. Register in `ToolingPlugin`
7. Update documentation
8. Test thoroughly

---

## Version History

### 1.0.0 (Current)
- Initial release
- 6 core tasks (hello, envSummary, dependencies, tasks, properties, deepClean)
- Full Kotlin DSL support
- Gradle 8.x compatibility

---

## License

[Your License Here]

## Support

For issues or questions:
- Check this README for usage examples
- Review task source code for details
- Contact development team

---

**Built with ❤️ using Gradle and Kotlin**

