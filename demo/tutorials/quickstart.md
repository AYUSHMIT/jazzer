# ⚡ Getting Started with Jazzer in 5 Minutes

<div align="center">

**🚀 From Zero to Fuzzing in Minutes**

[🏠 Demo Hub](../README.md) | [📚 All Tutorials](../tutorials)

</div>

---

## 🎯 What You'll Learn

In just 5 minutes, you'll:
1. ✅ Set up Jazzer in your project
2. ✅ Write your first fuzz test
3. ✅ Run it and see results
4. ✅ Understand the output

**Prerequisites:** Java 11+ and Maven or Gradle installed

---

## 📦 Step 1: Add Jazzer (30 seconds)

### Using Maven

Add this to your `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>com.code-intelligence</groupId>
        <artifactId>jazzer-junit</artifactId>
        <version>0.24.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Using Gradle

Add this to your `build.gradle`:

```gradle
dependencies {
    testImplementation 'com.code-intelligence:jazzer-junit:0.24.0'
}
```

---

## ✍️ Step 2: Write Your First Fuzz Test (2 minutes)

Create a new test file `src/test/java/com/example/MyFirstFuzzTest.java`:

```java
package com.example;

import com.code_intelligence.jazzer.junit.FuzzTest;
import com.code_intelligence.jazzer.mutation.annotation.NotNull;

class MyFirstFuzzTest {
    
    @FuzzTest
    void fuzzUrlParser(@NotNull String input) {
        // This is your fuzz test!
        // Jazzer will generate random strings and test your code
        
        try {
            // Let's test a simple URL parser
            parseUrl(input);
        } catch (IllegalArgumentException e) {
            // Expected for invalid URLs - this is OK
        }
    }
    
    // Simple URL parser (intentionally buggy for demo purposes)
    private void parseUrl(String url) {
        if (url.contains("://")) {
            String[] parts = url.split("://");
            
            // BUG: This will crash if there's nothing after ://
            if (parts[1].isEmpty()) {
                throw new IllegalArgumentException("Empty host");
            }
            
            // BUG: This will crash if parts[1] has special characters
            String host = parts[1].split("/")[0];
            
            // BUG: This will cause issues with certain inputs
            if (host.length() > 100) {
                throw new IllegalArgumentException("Host too long");
            }
        }
    }
}
```

---

## 🚀 Step 3: Run Your Fuzz Test (1 minute)

### Regression Mode (Default)
First, run in regression mode to verify everything works:

```bash
mvn test
```

**Expected output:**
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.example.MyFirstFuzzTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
```

### Fuzzing Mode (Find Bugs!)
Now enable fuzzing mode to discover bugs:

```bash
JAZZER_FUZZ=1 mvn test -Dtest=MyFirstFuzzTest
```

**Expected output (within seconds):**
```
== Java Exception: java.lang.IllegalArgumentException: Empty host
        at com.example.MyFirstFuzzTest.parseUrl(MyFirstFuzzTest.java:21)
        at com.example.MyFirstFuzzTest.fuzzUrlParser(MyFirstFuzzTest.java:13)

artifact_prefix='./'; Test unit written to crash-1234567890abcdef
```

🎉 **Congratulations!** Jazzer found a bug!

---

## 📊 Step 4: Understand the Results (1 minute)

### What Just Happened?

1. **Jazzer generated inputs** - Started with random strings
2. **Coverage feedback** - Tracked which code paths were executed
3. **Intelligent mutation** - Created new inputs based on what increased coverage
4. **Bug discovery** - Found an input that crashed your code
5. **Crash saved** - Stored the problematic input for replay

### Where Are the Results?

Jazzer creates two important directories:

```
.cifuzz-corpus/com.example.MyFirstFuzzTest/fuzzUrlParser/
└── <coverage-increasing-inputs>  ✅ Inputs that found new coverage

src/test/resources/com/example/MyFirstFuzzTestInputs/fuzzUrlParser/
└── crash-1234567890abcdef  ⚠️ Input that caused the crash
```

### Replay the Crash

To verify the fix later, just run:

```bash
mvn test
```

Jazzer will automatically replay crash inputs in regression mode!

---

## 🎯 What You've Learned

✅ **Added Jazzer to your project** with a single dependency  
✅ **Wrote a fuzz test** using the `@FuzzTest` annotation  
✅ **Ran fuzzing** in both regression and fuzzing modes  
✅ **Found a bug** automatically with intelligent input generation  
✅ **Understood the output** and where crash inputs are stored  

---

## 🚀 Next Steps

### Level Up Your Fuzzing Skills

1. **📖 Write Better Fuzz Tests**
   - Read [Writing Your First Fuzz Test](first-fuzz-test.md) for detailed guidance
   - Learn about [parameter annotations](../../docs/mutation-framework.md)

2. **🛡️ Add Security Sanitizers**
   - Explore [Sanitizer Configuration Guide](sanitizer-guide.md)
   - Try the [SQL Injection Demo](../examples/sql-injection-demo/)

3. **🔧 Integrate with CI/CD**
   - Set up [GitHub Actions](../integrations/github-actions/)
   - Automate fuzzing in your pipeline

4. **🚀 Advanced Techniques**
   - Learn [Advanced Fuzzing Techniques](advanced-techniques.md)
   - Optimize your fuzz tests for better results

---

## 💡 Quick Tips

### Do's ✅
- Start with simple fuzz tests
- Use `@NotNull` to avoid null inputs
- Catch expected exceptions
- Run in regression mode first
- Keep fuzz tests focused

### Don'ts ❌
- Don't fuzz code with random I/O
- Don't ignore crash inputs
- Don't test without assertions
- Don't fuzz initialization code
- Don't forget to version control corpus

---

## 🆘 Troubleshooting

### "No tests were found"
Make sure your test class and method are not `private`.

### "Native library not found"
On Linux, ensure `JAVA_HOME` is set correctly.

### "Test runs but finds no bugs"
That's good! Your code might be robust, or you may need to:
- Run longer (increase timeout)
- Add more complex logic to fuzz
- Check that sanitizers are enabled

---

## 🎪 Try More Examples

Ready for more? Check out these demos:

- 🗄️ [SQL Injection Detection](../examples/sql-injection-demo/)
- 📂 [Path Traversal Detection](../examples/path-traversal-demo/)
- 🌐 [SSRF Detection](../examples/ssrf-demo/)
- ⚡ [Command Injection Detection](../examples/command-injection-demo/)

---

## 📚 Resources

- 📖 [Jazzer Documentation](../../README.md)
- 🎓 [All Tutorials](../tutorials)
- 💡 [Live Examples](../examples)
- 🔧 [Integration Templates](../integrations)

---

<div align="center">

**Congratulations on your first fuzz test! 🎉**

[⬆ Back to Top](#-getting-started-with-jazzer-in-5-minutes) | [🏠 Demo Hub](../README.md)

</div>
