# ✍️ Writing Your First Fuzz Test

<div align="center">

**📖 A Step-by-Step Guide to Effective Fuzz Testing**

[🏠 Demo Hub](../README.md) | [📚 All Tutorials](../tutorials)

</div>

---

## 🎯 What You'll Learn

This comprehensive tutorial teaches you how to write effective fuzz tests:

- ✅ Understanding fuzz test anatomy
- ✅ Choosing what to fuzz
- ✅ Using parameter annotations
- ✅ Handling exceptions properly
- ✅ Maximizing coverage
- ✅ Interpreting results

**Time Required:** 15 minutes  
**Level:** Beginner  
**Prerequisites:** Completed [Quick Start](quickstart.md)

---

## 📚 Table of Contents

1. [Anatomy of a Fuzz Test](#-anatomy-of-a-fuzz-test)
2. [Choosing What to Fuzz](#-choosing-what-to-fuzz)
3. [Parameter Types](#-parameter-types)
4. [Using Annotations](#-using-annotations)
5. [Exception Handling](#-exception-handling)
6. [Testing with Assertions](#-testing-with-assertions)
7. [Practical Examples](#-practical-examples)
8. [Best Practices](#-best-practices)

---

## 🔬 Anatomy of a Fuzz Test

A fuzz test has three key components:

```java
import com.code_intelligence.jazzer.junit.FuzzTest;  // 1. Import

class MyFuzzTest {
    @FuzzTest  // 2. Annotation
    void myFuzzTest(String input) {  // 3. Test method with parameters
        // Your test logic here
        processInput(input);
    }
}
```

### Key Components:

1. **Import** - Brings in the `@FuzzTest` annotation
2. **Annotation** - Marks the method as a fuzz test
3. **Parameters** - Jazzer generates values for these automatically

---

## 🎯 Choosing What to Fuzz

### ✅ Good Candidates for Fuzzing

```java
// Parsers - Excellent for fuzzing!
@FuzzTest
void fuzzJsonParser(String json) {
    JsonParser.parse(json);
}

// Data validators - Perfect target
@FuzzTest
void fuzzEmailValidator(String email) {
    EmailValidator.isValid(email);
}

// Encoders/Decoders - Great for round-trip testing
@FuzzTest
void fuzzBase64(byte[] data) {
    String encoded = Base64.encode(data);
    byte[] decoded = Base64.decode(encoded);
    assertArrayEquals(data, decoded);
}

// String manipulation - Find edge cases
@FuzzTest
void fuzzUrlNormalizer(String url) {
    UrlNormalizer.normalize(url);
}
```

### ❌ Poor Candidates for Fuzzing

```java
// ❌ Random I/O - Non-deterministic
@FuzzTest
void badExample1(String filename) {
    File file = new File(filename);
    file.exists();  // Depends on file system state
}

// ❌ Network calls - Slow and unreliable
@FuzzTest
void badExample2(String url) {
    HttpClient.get(url);  // Network dependency
}

// ❌ Time-dependent - Non-reproducible
@FuzzTest
void badExample3(int delay) {
    Thread.sleep(delay);  // Time-dependent
}

// ❌ No logic - Nothing to test
@FuzzTest
void badExample4(String input) {
    // Empty - Jazzer has nothing to explore
}
```

---

## 🎨 Parameter Types

Jazzer supports many parameter types out of the box:

### Primitives

```java
@FuzzTest
void fuzzPrimitives(
    boolean flag,
    byte b,
    short s,
    int i,
    long l,
    float f,
    double d,
    char c
) {
    // Jazzer generates all primitive types
}
```

### Strings and Arrays

```java
@FuzzTest
void fuzzStringsAndArrays(
    String text,
    byte[] bytes,
    int[] numbers,
    String[] words
) {
    // Common types for fuzzing
}
```

### Collections

```java
@FuzzTest
void fuzzCollections(
    List<String> list,
    Set<Integer> set,
    Map<String, Integer> map
) {
    // Standard Java collections
}
```

### Complex Objects

```java
@FuzzTest
void fuzzObjects(
    URL url,
    URI uri,
    Duration duration,
    Instant instant
) {
    // Many standard library types supported
}
```

---

## 🏷️ Using Annotations

Annotations help guide Jazzer's input generation:

### @NotNull - Prevent Null Values

```java
import com.code_intelligence.jazzer.mutation.annotation.NotNull;

@FuzzTest
void fuzzNotNull(@NotNull String input) {
    // input is never null
    int length = input.length();  // Safe!
}
```

### @InRange - Constrain Numbers

```java
import com.code_intelligence.jazzer.mutation.annotation.InRange;

@FuzzTest
void fuzzInRange(@InRange(min = 1, max = 100) int count) {
    // count is always between 1 and 100
    int[] array = new int[count];  // Safe allocation!
}
```

### @WithUtf8Length - Control String Size

```java
import com.code_intelligence.jazzer.mutation.annotation.WithUtf8Length;

@FuzzTest
void fuzzWithLength(
    @NotNull @WithUtf8Length(min = 1, max = 50) String name
) {
    // name is 1-50 bytes in UTF-8, never null
    processName(name);
}
```

### Combining Annotations

```java
@FuzzTest
void fuzzCombined(
    @NotNull @WithUtf8Length(min = 5, max = 100) String username,
    @InRange(min = 18, max = 120) int age,
    @NotNull List<@NotNull String> tags
) {
    // Complex constraints for realistic inputs
    User user = new User(username, age, tags);
    user.validate();
}
```

---

## 🛡️ Exception Handling

### Strategy 1: Let Expected Exceptions Through

```java
@FuzzTest
void fuzzParser(String input) {
    try {
        Parser.parse(input);
    } catch (ParseException e) {
        // Expected for invalid input - OK to ignore
    }
    // Any OTHER exception will be caught by Jazzer!
}
```

### Strategy 2: Validate Exception Messages

```java
@FuzzTest
void fuzzValidation(String email) {
    try {
        EmailValidator.validate(email);
    } catch (ValidationException e) {
        // Make sure error messages don't leak sensitive info
        assertFalse(e.getMessage().contains("database"));
        assertFalse(e.getMessage().contains("internal"));
    }
}
```

### Strategy 3: Use Assertions

```java
@FuzzTest
void fuzzWithAssertions(String input) {
    Result result = processInput(input);
    
    // Test invariants
    assertNotNull(result);
    assertTrue(result.isValid() || result.hasError());
    
    // Validate properties
    if (result.isValid()) {
        assertNotNull(result.getData());
    }
}
```

---

## ✅ Testing with Assertions

### Property-Based Testing

```java
@FuzzTest
void fuzzEncodeDecodeRoundTrip(String original) {
    // Property: encode -> decode should return original
    String encoded = MyEncoder.encode(original);
    String decoded = MyEncoder.decode(encoded);
    assertEquals(original, decoded);
}
```

### Invariant Testing

```java
@FuzzTest
void fuzzSetInvariants(@NotNull List<Integer> numbers) {
    Set<Integer> set = new HashSet<>(numbers);
    
    // Invariant: size <= original list size
    assertTrue(set.size() <= numbers.size());
    
    // Invariant: all elements from set are in original list
    for (Integer num : set) {
        assertTrue(numbers.contains(num));
    }
}
```

### Security Properties

```java
@FuzzTest
void fuzzSanitization(@NotNull String userInput) {
    String sanitized = HtmlSanitizer.sanitize(userInput);
    
    // Property: no script tags in output
    assertFalse(sanitized.toLowerCase().contains("<script"));
    
    // Property: no event handlers
    assertFalse(sanitized.toLowerCase().matches(".*on\\w+\\s*=.*"));
}
```

---

## 💼 Practical Examples

### Example 1: URL Parser

```java
import com.code_intelligence.jazzer.junit.FuzzTest;
import com.code_intelligence.jazzer.mutation.annotation.NotNull;
import static org.junit.jupiter.api.Assertions.*;

class UrlParserFuzzTest {
    
    @FuzzTest
    void fuzzUrlParsing(@NotNull String url) {
        try {
            UrlComponents components = UrlParser.parse(url);
            
            // Test properties of parsed URL
            assertNotNull(components.getScheme());
            assertNotNull(components.getHost());
            
            // Test reconstruction
            String reconstructed = components.toString();
            
            // Property: parsing is consistent
            UrlComponents reparsed = UrlParser.parse(reconstructed);
            assertEquals(components, reparsed);
            
        } catch (MalformedUrlException e) {
            // Expected for malformed URLs
        }
    }
}
```

### Example 2: JSON Serialization

```java
class JsonSerializationFuzzTest {
    
    @FuzzTest
    void fuzzJsonRoundTrip(@NotNull Map<String, Object> data) {
        try {
            // Serialize
            String json = JsonSerializer.toJson(data);
            
            // Deserialize
            Map<String, Object> deserialized = JsonSerializer.fromJson(json);
            
            // Property: round-trip preserves data
            assertEquals(data, deserialized);
            
        } catch (JsonException e) {
            // Expected for non-serializable data
        }
    }
}
```

### Example 3: Input Validator

```java
class InputValidatorFuzzTest {
    
    @FuzzTest
    void fuzzInputValidation(
        @NotNull @WithUtf8Length(max = 200) String input
    ) {
        ValidationResult result = InputValidator.validate(input);
        
        // Test that validator doesn't crash
        assertNotNull(result);
        
        // Test security properties
        if (result.isValid()) {
            // Valid input should be safe
            String safe = result.getSanitized();
            assertFalse(safe.contains("<script"));
            assertFalse(safe.contains("javascript:"));
        } else {
            // Invalid input should have a reason
            assertNotNull(result.getErrorMessage());
            assertFalse(result.getErrorMessage().isEmpty());
        }
    }
}
```

---

## 🎯 Best Practices

### ✅ Do's

1. **Start Simple**
   ```java
   @FuzzTest
   void startSimple(String input) {
       MyClass.process(input);
   }
   ```

2. **Use Annotations to Guide Input**
   ```java
   @FuzzTest
   void useAnnotations(
       @NotNull @WithUtf8Length(min = 1, max = 100) String input
   ) {
       MyClass.process(input);
   }
   ```

3. **Test Properties and Invariants**
   ```java
   @FuzzTest
   void testProperties(String input) {
       String output = transform(input);
       assertTrue(output.length() >= input.length());
   }
   ```

4. **Handle Expected Exceptions**
   ```java
   @FuzzTest
   void handleExpected(String input) {
       try {
           parse(input);
       } catch (ParseException expected) {
           // OK
       }
   }
   ```

### ❌ Don'ts

1. **Don't Ignore All Exceptions**
   ```java
   @FuzzTest
   void dontIgnoreAll(String input) {
       try {
           process(input);
       } catch (Exception e) {
           // BAD: Might hide real bugs!
       }
   }
   ```

2. **Don't Test Side Effects**
   ```java
   @FuzzTest
   void dontTestSideEffects(String filename) {
       // BAD: File system side effects
       new File(filename).delete();
   }
   ```

3. **Don't Fuzz Async Code**
   ```java
   @FuzzTest
   void dontFuzzAsync(String data) {
       // BAD: Async makes fuzzing non-deterministic
       CompletableFuture.runAsync(() -> process(data));
   }
   ```

---

## 📊 Measuring Success

### Check Coverage

```bash
# Generate coverage report
JAZZER_COVERAGE=1 mvn test

# Look for:
# - New corpus entries in .cifuzz-corpus/
# - Coverage percentage
# - Crash inputs if bugs found
```

### Monitor Progress

```bash
# Run with verbose output
JAZZER_FUZZ=1 mvn test -Djazzer.valueprofile=1

# Watch for:
# - exec/s (executions per second)
# - cov (coverage increase)
# - corp (corpus size)
```

---

## 🚀 Next Steps

Now that you know how to write fuzz tests:

1. **🛡️ Add Security Testing** - [Sanitizer Configuration Guide](sanitizer-guide.md)
2. **🚀 Learn Advanced Techniques** - [Advanced Fuzzing Techniques](advanced-techniques.md)
3. **💡 Try Real Examples** - [SQL Injection Demo](../examples/sql-injection-demo/)
4. **🔧 Set Up CI/CD** - [GitHub Actions Integration](../integrations/github-actions/)

---

## 📚 Resources

- 📖 [Mutation Framework Details](../../docs/mutation-framework.md)
- 🎯 [Configuration Options](../../docs/arguments-and-configuration-options.md)
- 💡 [More Examples](../examples)
- 🏠 [Demo Hub](../README.md)

---

<div align="center">

**Happy Fuzzing! 🎉**

[⬆ Back to Top](#-writing-your-first-fuzz-test) | [🏠 Demo Hub](../README.md)

</div>
