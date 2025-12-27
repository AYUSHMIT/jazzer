# 🚀 Advanced Fuzzing Techniques

<div align="center">

**🎓 Master Advanced Fuzzing Patterns and Optimizations**

[🏠 Demo Hub](../README.md) | [📚 All Tutorials](../tutorials)

</div>

---

## 🎯 What You'll Learn

This advanced tutorial covers expert-level fuzzing techniques:

- ✅ Custom mutators and generators
- ✅ Coverage-guided fuzzing optimization
- ✅ Dictionary-based fuzzing
- ✅ Seed corpus management
- ✅ Performance tuning
- ✅ Multi-parameter fuzzing strategies

**Time Required:** 30 minutes  
**Level:** Advanced  
**Prerequisites:** Completed [Writing Your First Fuzz Test](first-fuzz-test.md)

---

## 📚 Table of Contents

1. [Custom Input Generation](#-custom-input-generation)
2. [Dictionary-Based Fuzzing](#-dictionary-based-fuzzing)
3. [Seed Corpus Management](#-seed-corpus-management)
4. [Coverage Optimization](#-coverage-optimization)
5. [Multi-Parameter Strategies](#-multi-parameter-strategies)
6. [Performance Tuning](#-performance-tuning)
7. [Debugging Fuzz Tests](#-debugging-fuzz-tests)
8. [Advanced Patterns](#-advanced-patterns)

---

## 🎨 Custom Input Generation

### Using FuzzedDataProvider

For complete control over input generation, use `FuzzedDataProvider`:

```java
import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;

class AdvancedFuzzTest {
    
    @FuzzTest
    void fuzzWithCustomLogic(FuzzedDataProvider data) {
        // Generate structured input
        int choice = data.consumeInt(0, 3);
        
        switch (choice) {
            case 0:
                testJsonInput(data);
                break;
            case 1:
                testXmlInput(data);
                break;
            case 2:
                testBinaryInput(data);
                break;
            default:
                testTextInput(data);
        }
    }
    
    private void testJsonInput(FuzzedDataProvider data) {
        // Build JSON structure
        String json = "{\n";
        int numFields = data.consumeInt(0, 10);
        
        for (int i = 0; i < numFields; i++) {
            String key = data.consumeString(20);
            String value = data.consumeString(50);
            json += "  \"" + key + "\": \"" + value + "\"";
            if (i < numFields - 1) json += ",";
            json += "\n";
        }
        json += "}";
        
        JsonParser.parse(json);
    }
}
```

### Structured Data Generation

```java
@FuzzTest
void fuzzComplexProtocol(FuzzedDataProvider data) {
    // Generate protocol message
    Message msg = new Message();
    
    // Header
    msg.setVersion(data.consumeByte());
    msg.setType(data.consumeInt(0, 255));
    msg.setFlags(data.consumeInt());
    
    // Body length
    int bodyLength = data.consumeInt(0, 1000);
    
    // Body
    if (data.remainingBytes() >= bodyLength) {
        byte[] body = data.consumeBytes(bodyLength);
        msg.setBody(body);
    }
    
    // Process the message
    processMessage(msg);
}
```

---

## 📖 Dictionary-Based Fuzzing

Dictionaries guide Jazzer toward interesting inputs like keywords, special strings, or magic values.

### Creating a Dictionary File

Create `.dict` file in your test resources:

```text
# src/test/resources/sql-keywords.dict

# SQL Keywords
"SELECT"
"INSERT"
"UPDATE"
"DELETE"
"DROP"
"UNION"
"WHERE"
"ORDER BY"

# SQL Injection patterns
"' OR '1'='1"
"'; DROP TABLE--"
"' UNION SELECT"
"admin'--"
"1' AND '1'='1"

# Special characters
"'"
"\""
";"
"--"
"/*"
"*/"
```

### Using the Dictionary

```java
import com.code_intelligence.jazzer.junit.DictEntries;

class SqlFuzzTest {
    
    @FuzzTest
    @DictEntries({
        "SELECT", "INSERT", "UPDATE", "DELETE",
        "' OR '1'='1", "'; DROP TABLE--"
    })
    void fuzzSqlQuery(String query) {
        // Jazzer will inject dictionary entries
        try {
            SqlParser.parse(query);
        } catch (SQLException e) {
            // Expected for malformed SQL
        }
    }
}
```

### Dynamic Dictionary Loading

```java
@FuzzTest
@DictEntries(file = "sql-keywords.dict")
void fuzzWithDictionaryFile(String input) {
    executeSqlQuery(input);
}
```

---

## 🌱 Seed Corpus Management

### Providing Seed Inputs

Use JUnit's parameter sources to provide seeds:

```java
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;

class SeededFuzzTest {
    
    @FuzzTest
    @ValueSource(strings = {
        "valid-input-1",
        "edge-case-input",
        "known-interesting-case",
        ""
    })
    void fuzzWithSeeds(String input) {
        // Jazzer starts with these seeds
        processInput(input);
    }
    
    @FuzzTest
    @MethodSource("complexSeeds")
    void fuzzComplexWithSeeds(ComplexInput input) {
        processComplex(input);
    }
    
    static Stream<ComplexInput> complexSeeds() {
        return Stream.of(
            new ComplexInput("test", 42, true),
            new ComplexInput("", 0, false),
            new ComplexInput("long string...", -1, true)
        );
    }
}
```

### Corpus Directory Structure

```
.cifuzz-corpus/
└── com.example.MyFuzzTest/
    └── myFuzzMethod/
        ├── seed-001  # Initial seeds
        ├── seed-002
        ├── abc123    # Generated inputs (coverage-increasing)
        └── def456
```

### Importing External Corpus

```bash
# Copy interesting inputs to corpus directory
mkdir -p .cifuzz-corpus/com.example.MyTest/fuzzMethod/
cp external-corpus/* .cifuzz-corpus/com.example.MyTest/fuzzMethod/

# Jazzer will use these as starting points
JAZZER_FUZZ=1 mvn test
```

---

## 📊 Coverage Optimization

### Value Profile for Magic Constants

Enable value profiling to help Jazzer find magic constants:

```bash
# Enable value profile
JAZZER_FUZZ=1 mvn test -Djazzer.valueprofile=1
```

Example where value profile helps:

```java
@FuzzTest
void fuzzMagicNumber(int value) {
    // Without value profile: takes forever to find 0xDEADBEEF
    // With value profile: found in seconds!
    if (value == 0xDEADBEEF) {
        throw new IllegalStateException("Magic number found!");
    }
}
```

### Reducing Execution Time

```java
@FuzzTest(maxDuration = "5m")  // Limit runtime
void fuzzFastPath(byte[] data) {
    if (data.length > 1000) {
        return;  // Skip expensive processing for large inputs
    }
    
    // Fast path
    quickProcess(data);
}
```

### Targeted Coverage

```java
@FuzzTest
void fuzzSpecificFeature(String input) {
    // Focus on specific feature
    if (!input.startsWith("COMMAND:")) {
        return;  // Skip uninteresting inputs early
    }
    
    // Process only relevant inputs
    String command = input.substring(8);
    executeCommand(command);
}
```

---

## 🎯 Multi-Parameter Strategies

### Coordinated Multi-Parameter Fuzzing

```java
@FuzzTest
void fuzzCoordinated(
    @NotNull @WithUtf8Length(max = 50) String username,
    @NotNull @WithUtf8Length(max = 100) String password,
    @InRange(min = 1, max = 3) int accountType
) {
    // Test user registration with coordinated inputs
    User user = new User(username, password, accountType);
    
    // Validate invariants
    assertTrue(user.getUsername().equals(username));
    assertTrue(user.getAccountType() >= 1 && user.getAccountType() <= 3);
    
    // Test authentication
    AuthService.register(user);
    assertTrue(AuthService.authenticate(username, password));
}
```

### State Machine Fuzzing

```java
@FuzzTest
void fuzzStateMachine(FuzzedDataProvider data) {
    StatefulComponent component = new StatefulComponent();
    
    // Generate sequence of operations
    int numOperations = data.consumeInt(1, 20);
    
    for (int i = 0; i < numOperations && data.remainingBytes() > 0; i++) {
        int operation = data.consumeInt(0, 4);
        
        switch (operation) {
            case 0:
                component.initialize(data.consumeString(10));
                break;
            case 1:
                component.addData(data.consumeBytes(50));
                break;
            case 2:
                component.process();
                break;
            case 3:
                component.reset();
                break;
            default:
                component.close();
        }
        
        // Check invariants after each operation
        assertFalse(component.isInInvalidState());
    }
}
```

---

## ⚡ Performance Tuning

### Maximizing Executions Per Second

```java
class PerformanceTunedFuzzTest {
    
    // Reuse objects to reduce allocation overhead
    private static final ThreadLocal<Parser> PARSER = 
        ThreadLocal.withInitial(Parser::new);
    
    @FuzzTest
    void fuzzOptimized(String input) {
        // Reuse parser instance
        Parser parser = PARSER.get();
        parser.reset();
        parser.parse(input);
    }
}
```

### Minimize Setup/Teardown

```java
class EfficientFuzzTest {
    
    // One-time setup
    private static final Database DB = Database.inMemory();
    
    @FuzzTest
    void fuzzQuery(String query) {
        // No per-iteration setup
        // Use in-memory database
        DB.execute(query);
        DB.rollback();  // Quick cleanup
    }
}
```

### Parallel Fuzzing

```bash
# Run multiple fuzzing jobs in parallel
parallel -j 4 'JAZZER_FUZZ=1 mvn test -Dtest=MyFuzzTest &' ::: {1..4}
```

---

## 🔍 Debugging Fuzz Tests

### Reproducing Failures

```java
class DebugFuzzTest {
    
    @Test
    void reproduceCrash() throws Exception {
        // Read crash input
        byte[] crashInput = Files.readAllBytes(
            Paths.get("src/test/resources/MyFuzzTestInputs/fuzzMethod/crash-abc123")
        );
        
        // Replay with debugger
        fuzzMethod(crashInput);
    }
    
    @FuzzTest
    void fuzzMethod(byte[] data) {
        // Set breakpoint here when running reproduceCrash
        processData(data);
    }
}
```

### Logging and Diagnostics

```java
@FuzzTest
void fuzzWithLogging(String input) {
    // Conditional logging for debugging
    boolean debug = System.getenv("JAZZER_DEBUG") != null;
    
    if (debug) {
        System.err.println("Processing: " + input);
    }
    
    try {
        Result result = process(input);
        
        if (debug) {
            System.err.println("Result: " + result);
        }
    } catch (Exception e) {
        if (debug) {
            System.err.println("Exception: " + e.getMessage());
        }
        throw e;
    }
}
```

### Minimizing Crash Inputs

```bash
# Jazzer automatically minimizes crashes
# The saved crash input is usually already minimal

# For manual minimization:
java -cp jazzer.jar:your-app.jar \
  com.code_intelligence.jazzer.Jazzer \
  --target_class=com.example.MyFuzzTest \
  --minimize_crash=crash-abc123
```

---

## 🎨 Advanced Patterns

### Pattern 1: Differential Fuzzing

Test two implementations against each other:

```java
@FuzzTest
void fuzzDifferential(String input) {
    // Test that two implementations agree
    Result result1 = Implementation1.process(input);
    Result result2 = Implementation2.process(input);
    
    assertEquals(result1, result2, 
        "Implementations disagree on: " + input);
}
```

### Pattern 2: Round-Trip Testing

```java
@FuzzTest
void fuzzRoundTrip(ComplexObject obj) {
    // Serialize
    byte[] serialized = Serializer.serialize(obj);
    
    // Deserialize
    ComplexObject deserialized = Serializer.deserialize(serialized);
    
    // Must be equal
    assertEquals(obj, deserialized);
}
```

### Pattern 3: Invariant Fuzzing

```java
@FuzzTest
void fuzzInvariants(List<Integer> operations) {
    DataStructure ds = new DataStructure();
    
    for (Integer op : operations) {
        // Apply operation
        ds.performOperation(op);
        
        // Check invariants after each step
        assertTrue(ds.size() >= 0);
        assertTrue(ds.checksum() == ds.calculateChecksum());
        assertEquals(ds.size(), ds.count());
    }
}
```

### Pattern 4: Security Property Testing

```java
@FuzzTest
void fuzzSecurityProperties(@NotNull String userInput) {
    // Process user input
    String output = SecurityProcessor.process(userInput);
    
    // Security invariants must hold
    assertFalse(output.contains("<script"), 
        "XSS vulnerability: script tag in output");
    assertFalse(output.matches(".*javascript:.*"), 
        "XSS vulnerability: javascript: protocol");
    assertFalse(output.contains("'; DROP TABLE"), 
        "SQL injection vulnerability");
    
    // Output must be properly encoded
    assertTrue(output.chars().allMatch(c -> c < 128 || isValidUtf8(c)));
}
```

---

## 🎯 Best Practices Summary

### ✅ Do

- **Use dictionaries** for domain-specific fuzzing
- **Provide seeds** for interesting starting points
- **Enable value profile** for magic constants
- **Reuse objects** to improve performance
- **Test invariants** after each operation
- **Minimize inputs** early with fast checks

### ❌ Don't

- **Don't fuzz without assertions** - You won't find logic bugs
- **Don't create expensive objects** in hot path
- **Don't ignore performance** - Faster = more coverage
- **Don't forget to clean up** state between iterations
- **Don't test too many things** in one fuzz test

---

## 🚀 Real-World Example

Here's a complete advanced fuzz test for a URL parser:

```java
class UrlParserAdvancedFuzzTest {
    
    // Dictionary for URL fuzzing
    private static final String[] URL_PARTS = {
        "http://", "https://", "ftp://",
        "localhost", "127.0.0.1", "example.com",
        "8080", "443", "80",
        "/path", "/path/to/resource", "",
        "?query=value", "#fragment"
    };
    
    @FuzzTest
    @ValueSource(strings = {
        "http://example.com",
        "https://localhost:8080/path?query=1#frag",
        "ftp://user:pass@host:21/file",
    })
    void fuzzUrlParserAdvanced(FuzzedDataProvider data) {
        // Build URL from components
        StringBuilder url = new StringBuilder();
        
        // Scheme
        url.append(data.pickValue(URL_PARTS));
        
        // Host
        if (data.consumeBoolean()) {
            url.append("user:pass@");
        }
        url.append(data.pickValue(URL_PARTS));
        
        // Port
        if (data.consumeBoolean()) {
            url.append(":").append(data.consumeInt(1, 65535));
        }
        
        // Path
        url.append(data.pickValue(URL_PARTS));
        
        // Query
        if (data.consumeBoolean()) {
            url.append(data.pickValue(URL_PARTS));
        }
        
        // Fragment
        if (data.consumeBoolean()) {
            url.append(data.pickValue(URL_PARTS));
        }
        
        String urlString = url.toString();
        
        try {
            // Parse
            URL parsed = UrlParser.parse(urlString);
            
            // Test properties
            assertNotNull(parsed.getScheme());
            assertNotNull(parsed.getHost());
            
            // Test reconstruction
            String reconstructed = parsed.toString();
            URL reparsed = UrlParser.parse(reconstructed);
            
            // Parsing should be stable
            assertEquals(parsed, reparsed);
            
        } catch (MalformedUrlException e) {
            // Expected for malformed URLs
        }
    }
}
```

---

## 📚 Further Reading

- 📖 [Mutation Framework](../../docs/mutation-framework.md)
- 🎯 [Configuration Options](../../docs/arguments-and-configuration-options.md)
- 🛡️ [Sanitizer Guide](sanitizer-guide.md)
- 💡 [Live Examples](../examples)

---

<div align="center">

**Master the Art of Fuzzing! 🚀**

[⬆ Back to Top](#-advanced-fuzzing-techniques) | [🏠 Demo Hub](../README.md)

</div>
