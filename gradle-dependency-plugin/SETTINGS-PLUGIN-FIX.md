# Settings Plugin Fix - UnsupportedNotationException Resolution

## Problem
The consumer build was failing with:
```
org.gradle.internal.typeconversion.UnsupportedNotationException: 
Cannot convert the provided notation to an object of type Dependency: 
/Users/.../build/generated/catalogs/libs.versions.toml
```

## Root Cause
In `DependencySettingsPlugin.kt`, the version catalog was being configured with:
```kotlin
from(generated)  // File directly - causes type ambiguity
```

When Gradle's version catalog API received a raw `File` object, it was being misinterpreted as a dependency notation instead of a file path for the catalog source.

## Solution Applied
Changed `DependencySettingsPlugin.kt` to inject `ObjectFactory` and create a proper `ConfigurableFileCollection`:

```kotlin
abstract class DependencySettingsPlugin @Inject constructor(
    private val objects: ObjectFactory
) : Plugin<Settings> {
    override fun apply(settings: Settings) {
        // ... existing code ...
        
        versionCatalogs {
            create("libs") {
                // Use ObjectFactory to create proper FileCollection
                from(objects.fileCollection().from(generated))
            }
        }
    }
}
```

### Why This Works
- `ObjectFactory.fileCollection()` creates a `ConfigurableFileCollection`
- `.from(generated)` adds the File to the collection
- The version catalog API correctly recognizes this as a file source (not a dependency)
- No ambiguity with dependency notation APIs

## Files Changed
1. **DependencySettingsPlugin.kt**
   - Made class abstract to support `@Inject` constructor
   - Injected `ObjectFactory`
   - Changed `from(generated)` to `from(objects.fileCollection().from(generated))`

2. **Plugin published to mavenLocal**
   - Group: `com.gradle.dependency.plugin`
   - Artifact: `gradle-dependency-plugin`
   - Version: `1.0.2`

## Consumer Configuration
The consumer's `settings.gradle.kts` should look like:

```kotlin
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("com.gradle.dependency.settings") version "1.0.2"
}

rootProject.name = "gradle-plugin-consumer"
```

The consumer's `build.gradle.kts`:
```kotlin
plugins {
    id("com.gradle.dependency.plugin") version "1.0.2"
    id("com.gradle.tooling.plugin") version "1.0.1"
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    testImplementation(libs.spring.boot.starter.test)
}
```

## How to Verify the Fix

### Step 1: Verify Plugin Published
```bash
ls -la ~/.m2/repository/com/gradle/dependency/plugin/gradle-dependency-plugin/1.0.2/
```

You should see:
- `gradle-dependency-plugin-1.0.2.jar`
- `gradle-dependency-plugin-1.0.2.pom`
- `gradle-dependency-plugin-1.0.2.module`

### Step 2: Clean and Build Consumer
```bash
cd /Users/rekhadevakumar/Desktop/workspace/gradle-plugins-suite/gradle-plugin-consumer

# Clear Gradle caches to ensure fresh plugin resolution
./gradlew --stop
rm -rf ~/.gradle/caches/modules-2/files-2.1/com.gradle.dependency.plugin

# Run the build
./gradlew clean build --refresh-dependencies --info
```

### Step 3: Verify Success
Look for these indicators in the output:

1. **Settings Plugin Applied:**
   ```
   📘 DependencySettingsPlugin applied: version catalog 'libs' installed from plugin resources
   ```

2. **Plugin Resolution:**
   ```
   Resolved plugin [id: 'com.gradle.dependency.settings', version: '1.0.2']
   ```

3. **Build Success:**
   ```
   BUILD SUCCESSFUL in Xs
   ```

4. **No UnsupportedNotationException** in the output

### Step 4: Verify Catalog Works
The consumer should successfully resolve dependencies like:
```kotlin
implementation(libs.spring.boot.starter.web)
```

If you see "Unresolved reference: libs", the catalog wasn't installed correctly.

## Troubleshooting

### If Plugin Not Found
```bash
# Check if published to mavenLocal
find ~/.m2/repository -name "gradle-dependency-plugin-1.0.2.jar" -print

# If not found, republish:
cd /Users/rekhadevakumar/Desktop/workspace/gradle-plugins-suite/gradle-dependency-plugin
./gradlew clean publishToMavenLocal
```

### If Still Getting UnsupportedNotationException
1. Verify the plugin version in `settings.gradle.kts` matches published version (1.0.2)
2. Clear Gradle caches: `rm -rf ~/.gradle/caches`
3. Force refresh: `./gradlew build --refresh-dependencies`

### If Catalog Not Available (Unresolved reference: libs)
1. Check that settings plugin was applied (look for 📘 log message)
2. Verify `build/generated/catalogs/libs.versions.toml` exists in consumer project after settings evaluation
3. Check the plugin JAR contains `catalogs/libs.versions.toml` resource:
   ```bash
   unzip -l ~/.m2/repository/com/gradle/dependency/plugin/gradle-dependency-plugin/1.0.2/gradle-dependency-plugin-1.0.2.jar | grep libs.versions.toml
   ```

## Summary
✅ Fixed: `from(generated)` → `from(objects.fileCollection().from(generated))`
✅ Plugin compiles without errors
✅ Plugin published to mavenLocal as version 1.0.2
✅ Consumer configured to use settings plugin from mavenLocal

The fix ensures Gradle's version catalog API receives a properly typed `ConfigurableFileCollection` instead of a raw `File`, eliminating the type ambiguity that caused the `UnsupportedNotationException`.

