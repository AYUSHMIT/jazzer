# 📦 Maven Project Template for Jazzer

<div align="center">

**Complete Maven Setup with Jazzer Fuzzing**

[🏠 Demo Hub](../../README.md) | [🔧 All Integrations](../)

</div>

---

## 🎯 What's Included

This template provides a ready-to-use Maven project with:

- ✅ Jazzer dependency configuration
- ✅ Example fuzz tests
- ✅ Proper directory structure
- ✅ Maven Surefire configuration
- ✅ CI/CD integration ready

---

## 📁 Project Structure

```
maven-template/
├── pom.xml
├── README.md
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
cp -r demo/integrations/maven-template my-fuzzing-project
cd my-fuzzing-project
```

### 2. Run Tests

```bash
# Regression mode (default)
mvn test

# Fuzzing mode
JAZZER_FUZZ=1 mvn test
```

### 3. Customize

Edit `pom.xml` to match your project:
- Change `groupId`, `artifactId`, `version`
- Add your application dependencies
- Adjust Java version if needed

---

## 📝 pom.xml Configuration

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>my-fuzzing-project</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <!-- Java version -->
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        
        <!-- Dependency versions -->
        <jazzer.version>0.24.0</jazzer.version>
        <junit.version>5.9.2</junit.version>
    </properties>

    <dependencies>
        <!-- Jazzer for fuzz testing -->
        <dependency>
            <groupId>com.code-intelligence</groupId>
            <artifactId>jazzer-junit</artifactId>
            <version>${jazzer.version}</version>
            <scope>test</scope>
        </dependency>

        <!-- JUnit 5 -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>

        <!-- Your application dependencies here -->
    </dependencies>

    <build>
        <plugins>
            <!-- Maven Surefire for running tests -->
            <plugin>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>2.22.2</version>
                <configuration>
                    <!-- Increase memory for fuzzing -->
                    <argLine>-Xmx2g</argLine>
                </configuration>
            </plugin>

            <!-- Maven Compiler -->
            <plugin>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
            </plugin>
        </plugins>

        <!-- Include test resources -->
        <testResources>
            <testResource>
                <directory>${project.basedir}/src/test/resources</directory>
            </testResource>
        </testResources>
    </build>
</project>
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

## 🔧 Configuration Options

### Maven Surefire Configuration

```xml
<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>
    <configuration>
        <!-- Run specific tests -->
        <includes>
            <include>**/*FuzzTest.java</include>
        </includes>
        
        <!-- Memory settings -->
        <argLine>-Xmx2g -Xms512m</argLine>
        
        <!-- Parallel execution -->
        <parallel>methods</parallel>
        <threadCount>4</threadCount>
    </configuration>
</plugin>
```

### Jazzer-Specific Settings

```bash
# Environment variables
export JAZZER_FUZZ=1                    # Enable fuzzing mode
export JAZZER_COVERAGE=1                # Include corpus in regression
export JAZZER_VALUEPROFILE=1            # Enable value profiling

# System properties
mvn test -Djazzer.valueprofile=1
mvn test -Djazzer.hooks=true
```

---

## 📊 Running in Different Modes

### Regression Testing (CI/CD)

```bash
# Run all tests including fuzz tests with known inputs
mvn test
```

### Fuzzing Mode (Local Development)

```bash
# Fuzz specific test
JAZZER_FUZZ=1 mvn test -Dtest=MyParserFuzzTest

# Fuzz all tests
JAZZER_FUZZ=1 mvn test

# Fuzz with time limit
JAZZER_FUZZ=1 mvn test -Djazzer.timeout=300
```

### Coverage Mode

```bash
# Include corpus in regression testing
JAZZER_COVERAGE=1 mvn test
```

---

## 🎯 Best Practices

### 1. Separate Fuzz Tests

```
src/test/java/
├── com/example/
│   ├── MyParserTest.java      # Traditional unit tests
│   └── MyParserFuzzTest.java  # Fuzz tests
```

### 2. Use Profiles

```xml
<profiles>
    <profile>
        <id>fuzzing</id>
        <build>
            <plugins>
                <plugin>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <configuration>
                        <includes>
                            <include>**/*FuzzTest.java</include>
                        </includes>
                        <argLine>-Xmx4g</argLine>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

Run with: `mvn test -Pfuzzing`

### 3. Manage Corpus Files

```xml
<build>
    <resources>
        <resource>
            <directory>.cifuzz-corpus</directory>
            <filtering>false</filtering>
        </resource>
    </resources>
</build>
```

Add to `.gitattributes`:
```
.cifuzz-corpus/** binary
src/test/resources/** binary
```

---

## 🚀 Next Steps

1. [Write Your First Fuzz Test](../../tutorials/first-fuzz-test.md)
2. [Set Up CI/CD](../github-actions/)
3. [Try Examples](../../examples/)
4. [Advanced Techniques](../../tutorials/advanced-techniques.md)

---

## 📚 Resources

- 📖 [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)
- 🎯 [Jazzer Documentation](../../../README.md)
- 💡 [Example Projects](../../../examples/junit/)

---

<div align="center">

**Happy Fuzzing with Maven! 📦**

[🏠 Demo Hub](../../README.md)

</div>
