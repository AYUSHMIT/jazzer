# 🐘 Gradle Project Template for Jazzer

<div align="center">

**Complete Gradle Setup with Jazzer Fuzzing**

[🏠 Demo Hub](../../README.md) | [🔧 All Integrations](../)

</div>

---

## 🎯 What's Included

This template provides a ready-to-use Gradle project with:

- ✅ Jazzer dependency configuration
- ✅ Gradle test tasks
- ✅ Example fuzz tests
- ✅ Kotlin DSL support
- ✅ CI/CD integration ready

---

## 📁 Project Structure

```
gradle-template/
├── build.gradle (or build.gradle.kts for Kotlin DSL)
├── settings.gradle
├── gradle.properties
└── src/
    ├── main/java/com/example/
    │   └── MyParser.java
    └── test/java/com/example/
        └── MyParserFuzzTest.java
```

---

## 🚀 Quick Start

### 1. Copy the Template

```bash
cp -r demo/integrations/gradle-template my-fuzzing-project
cd my-fuzzing-project
```

### 2. Run Tests

```bash
# Regression mode
./gradlew test

# Fuzzing mode
JAZZER_FUZZ=1 ./gradlew test
```

---

## 📝 build.gradle Configuration

### Groovy DSL

```gradle
plugins {
    id 'java'
}

group = 'com.example'
version = '1.0-SNAPSHOT'

sourceCompatibility = '11'
targetCompatibility = '11'

repositories {
    mavenCentral()
}

dependencies {
    // Jazzer for fuzz testing
    testImplementation 'com.code-intelligence:jazzer-junit:0.24.0'
    
    // JUnit 5
    testImplementation 'org.junit.jupiter:junit-jupiter:5.9.2'
    
    // Your application dependencies here
}

test {
    useJUnitPlatform()
    
    // Increase memory for fuzzing
    maxHeapSize = '2g'
    
    // Pass environment variables
    environment System.getenv()
    
    // Test output
    testLogging {
        events "passed", "skipped", "failed"
        exceptionFormat "full"
    }
}

// Custom task for fuzzing
task fuzzTest(type: Test) {
    useJUnitPlatform()
    
    // Only run fuzz tests
    include '**/*FuzzTest.class'
    
    // Set fuzzing mode
    environment 'JAZZER_FUZZ', '1'
    
    // More memory for fuzzing
    maxHeapSize = '4g'
    
    testLogging {
        events "passed", "skipped", "failed"
        showStandardStreams = true
    }
}
```

### Kotlin DSL (build.gradle.kts)

```kotlin
plugins {
    java
}

group = "com.example"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
}

dependencies {
    // Jazzer for fuzz testing
    testImplementation("com.code-intelligence:jazzer-junit:0.24.0")
    
    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

tasks.test {
    useJUnitPlatform()
    
    // Increase memory for fuzzing
    maxHeapSize = "2g"
    
    // Pass environment variables
    environment(System.getenv())
    
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

// Custom task for fuzzing
tasks.register<Test>("fuzzTest") {
    useJUnitPlatform()
    
    // Only run fuzz tests
    include("**/*FuzzTest.class")
    
    // Set fuzzing mode
    environment("JAZZER_FUZZ", "1")
    
    // More memory for fuzzing
    maxHeapSize = "4g"
    
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}
```

---

## ✍️ Example Fuzz Test

```java
package com.example;

import com.code_intelligence.jazzer.junit.FuzzTest;
import com.code_intelligence.jazzer.mutation.annotation.NotNull;

class MyParserFuzzTest {
    
    @FuzzTest(maxDuration = "1m")
    void fuzzParse(@NotNull String input) {
        try {
            MyParser.parse(input);
        } catch (ParseException e) {
            // Expected for invalid input
        }
    }
}
```

---

## 🔧 Gradle Tasks

### Built-in Tasks

```bash
# Run all tests (regression mode)
./gradlew test

# Run specific test class
./gradlew test --tests MyParserFuzzTest

# Run with fuzzing mode
JAZZER_FUZZ=1 ./gradlew test

# Clean and test
./gradlew clean test
```

### Custom Fuzzing Task

```bash
# Run only fuzz tests in fuzzing mode
./gradlew fuzzTest

# Run with more verbose output
./gradlew fuzzTest --info

# Run with debug output
./gradlew fuzzTest --debug
```

---

## 📊 Advanced Configuration

### Parallel Test Execution

```gradle
test {
    useJUnitPlatform()
    
    // Run tests in parallel
    maxParallelForks = Runtime.runtime.availableProcessors().intdiv(2) ?: 1
    
    // Separate JVM per test class
    forkEvery = 1
}
```

### JVM Arguments

```gradle
test {
    // JVM arguments for all tests
    jvmArgs = [
        '-Xmx2g',
        '-XX:+UseG1GC',
        '-Djazzer.valueprofile=1'
    ]
}
```

### Test Filtering

```gradle
test {
    useJUnitPlatform {
        // Include/exclude tests by tags
        includeTags 'fast'
        excludeTags 'slow'
    }
    
    // Filter by pattern
    filter {
        includeTestsMatching '*FuzzTest'
    }
}
```

---

## 🎯 Best Practices

### 1. Separate Source Sets

```gradle
sourceSets {
    fuzzTest {
        java {
            srcDir 'src/fuzz-test/java'
        }
        resources {
            srcDir 'src/fuzz-test/resources'
        }
        compileClasspath += sourceSets.main.output + sourceSets.test.output
        runtimeClasspath += sourceSets.main.output + sourceSets.test.output
    }
}
```

### 2. Custom Test Reports

```gradle
test {
    reports {
        html.enabled = true
        junitXml.enabled = true
    }
    
    finalizedBy jacocoTestReport  // Generate coverage
}
```

### 3. Environment-Specific Configuration

```gradle
// gradle.properties
jazzer.fuzz=false
jazzer.timeout=300

// build.gradle
test {
    if (project.hasProperty('jazzer.fuzz') && project.property('jazzer.fuzz') == 'true') {
        environment 'JAZZER_FUZZ', '1'
    }
}
```

Run with: `./gradlew test -Pjazzer.fuzz=true`

---

## 🚀 CI/CD Integration

### GitHub Actions

```yaml
- name: Run Fuzz Tests
  run: ./gradlew fuzzTest
  env:
    JAZZER_FUZZ: 1
```

### GitLab CI

```yaml
fuzz-test:
  script:
    - ./gradlew fuzzTest
  variables:
    JAZZER_FUZZ: "1"
```

---

## 📦 Multi-Module Projects

```gradle
// Root build.gradle
subprojects {
    apply plugin: 'java'
    
    repositories {
        mavenCentral()
    }
    
    dependencies {
        testImplementation 'com.code-intelligence:jazzer-junit:0.24.0'
        testImplementation 'org.junit.jupiter:junit-jupiter:5.9.2'
    }
    
    test {
        useJUnitPlatform()
    }
}
```

---

## 🚀 Next Steps

1. [Write Your First Fuzz Test](../../tutorials/first-fuzz-test.md)
2. [Set Up CI/CD](../github-actions/)
3. [Try Examples](../../examples/)
4. [Advanced Techniques](../../tutorials/advanced-techniques.md)

---

## 📚 Resources

- 📖 [Gradle Test Task](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.testing.Test.html)
- 🎯 [Jazzer Documentation](../../../README.md)
- 💡 [JUnit 5 on Gradle](https://docs.gradle.org/current/userguide/java_testing.html#using_junit5)

---

<div align="center">

**Happy Fuzzing with Gradle! 🐘**

[🏠 Demo Hub](../../README.md)

</div>
