# 🎯 DEMO.md - Jazzer Interactive Demo Showcase

<div align="center">

```
     ██╗ █████╗ ███████╗███████╗███████╗██████╗ 
     ██║██╔══██╗╚══███╔╝╚══███╔╝██╔════╝██╔══██╗
     ██║███████║  ███╔╝   ███╔╝ █████╗  ██████╔╝
██   ██║██╔══██║ ███╔╝   ███╔╝  ██╔══╝  ██╔══██╗
╚█████╔╝██║  ██║███████╗███████╗███████╗██║  ██║
 ╚════╝ ╚═╝  ╚═╝╚══════╝╚══════╝╚══════╝╚═╝  ╚═╝
```

**🎪 Welcome to the Jazzer Demo Showcase!**

*Explore fuzzing through interactive examples, tutorials, and ready-to-use templates*

[![Demo Hub](https://img.shields.io/badge/📚-Demo_Hub-blue?style=for-the-badge)](demo/README.md)
[![Quick Start](https://img.shields.io/badge/⚡-Quick_Start-green?style=for-the-badge)](demo/tutorials/quickstart.md)
[![Examples](https://img.shields.io/badge/💡-Live_Examples-orange?style=for-the-badge)](demo/examples/)

</div>

---

## 🚀 Try It Now - 30 Second Quick Start

```bash
# 1. Clone the demo
git clone https://github.com/AYUSHMIT/jazzer.git
cd jazzer/demo/examples/sql-injection-demo

# 2. Run fuzzing (finds bugs in seconds!)
JAZZER_FUZZ=1 mvn test

# 3. Watch Jazzer discover SQL injection vulnerabilities automatically!
```

---

## 🎨 What's Inside?

### 💡 Live Security Examples

Try working security vulnerability detection demos:

| 🎯 Demo | What It Shows | Try It |
|---------|---------------|--------|
| **🗄️ SQL Injection** | Database query security | `cd demo/examples/sql-injection-demo && mvn test` |
| **📂 Path Traversal** | File system security | `cd demo/examples/path-traversal-demo && mvn test` |
| **🌐 SSRF Detection** | Network request security | `cd demo/examples/ssrf-demo && mvn test` |
| **⚡ Command Injection** | OS command security | `cd demo/examples/command-injection-demo && mvn test` |
| **🔓 Deserialization** | Object security | `cd demo/examples/deserialization-demo && mvn test` |

Each demo includes:
- ✅ Complete working code
- ✅ Before/after comparisons
- ✅ Sample attack inputs
- ✅ Detailed explanations

[📖 Explore All Examples →](demo/examples/)

---

### 🎓 Interactive Tutorials

Learn fuzzing step-by-step:

1. **⚡ Getting Started (5 minutes)**
   - Set up Jazzer in your project
   - Write your first fuzz test
   - Find your first bug!
   - [Start Tutorial →](demo/tutorials/quickstart.md)

2. **✍️ Writing Fuzz Tests (15 minutes)**
   - Understand fuzz test anatomy
   - Use parameter annotations
   - Handle exceptions properly
   - Test with assertions
   - [Start Tutorial →](demo/tutorials/first-fuzz-test.md)

3. **🚀 Advanced Techniques (30 minutes)**
   - Custom input generation
   - Dictionary-based fuzzing
   - Corpus management
   - Performance optimization
   - [Start Tutorial →](demo/tutorials/advanced-techniques.md)

4. **🛡️ Security Sanitizers (20 minutes)**
   - Configure bug detectors
   - Set up allowlists
   - Detect vulnerabilities automatically
   - [Start Tutorial →](demo/tutorials/sanitizer-guide.md)

---

### 🔧 Ready-to-Use Templates

Drop these into your project and start fuzzing:

- **📦 Maven Template** - Complete pom.xml with Jazzer configured
  - [View Template →](demo/integrations/maven-template/)
  - Just copy and customize for your project

- **🐘 Gradle Template** - Both Groovy and Kotlin DSL
  - [View Template →](demo/integrations/gradle-template/)
  - Includes custom fuzzing tasks

- **🔄 GitHub Actions** - Automated CI/CD fuzzing
  - [View Workflows →](demo/integrations/github-actions/)
  - Copy-paste ready workflows

---

### 📊 Comparisons & Visualizations

See fuzzing in action:

- **🆚 Traditional vs Fuzz Testing**
  - Side-by-side code comparison
  - Bug discovery statistics
  - When to use each approach
  - [Read Comparison →](demo/comparisons/traditional-vs-fuzzing/)

- **📈 Architecture Diagrams**
  - [System Architecture](demo/visualizations/architecture-diagram.mmd)
  - [Fuzzing Workflow](demo/visualizations/fuzzing-workflow.mmd)
  - [Sanitizer Flow](demo/visualizations/sanitizer-flow.mmd)

---

## 🎯 Choose Your Learning Path

<table>
<tr>
<td width="33%" align="center">

### 🌱 Beginner

**New to fuzzing?**

1. [5-Minute Quick Start](demo/tutorials/quickstart.md)
2. [SQL Injection Demo](demo/examples/sql-injection-demo/)
3. [Writing Fuzz Tests](demo/tutorials/first-fuzz-test.md)

</td>
<td width="33%" align="center">

### 🚀 Intermediate

**Ready to dive deeper?**

1. [Advanced Techniques](demo/tutorials/advanced-techniques.md)
2. [Sanitizer Config](demo/tutorials/sanitizer-guide.md)
3. [SSRF Demo](demo/examples/ssrf-demo/)

</td>
<td width="33%" align="center">

### 🎓 Expert

**Mastering fuzzing?**

1. [All Security Demos](demo/examples/)
2. [CI/CD Integration](demo/integrations/github-actions/)
3. [Custom Mutators](docs/mutation-framework.md)

</td>
</tr>
</table>

---

## 🏆 Real Impact - Bugs Found by Jazzer

Jazzer has discovered critical vulnerabilities in major projects:

| Project | CVE | Impact | Severity |
|---------|-----|--------|----------|
| Protocol Buffers | CVE-2021-22569 | DoS | 🔴 High |
| Apache Commons IO | CVE-2021-29425 | Path Traversal | 🔴 High |
| Google Gson | CVE-2022-25647 | DoS | 🟡 Medium |
| Jackson | CVE-2022-42003 | DoS | 🟡 Medium |

[View Full Trophy List →](docs/trophies.md)

**Your project could be next to find and fix critical bugs!**

---

## 💡 Interactive Features

### 🎮 Choose Your Security Concern

Click to jump to the relevant demo:

<div align="center">

[🗄️ Database Security](demo/examples/sql-injection-demo/) • 
[📂 File System](demo/examples/path-traversal-demo/) • 
[🌐 Network](demo/examples/ssrf-demo/) • 
[⚡ Commands](demo/examples/command-injection-demo/) • 
[🔓 Deserialization](demo/examples/deserialization-demo/)

</div>

### 📈 By the Numbers

- **🚀 1000-10000** executions per second
- **📊 90%+** code coverage typically achieved
- **🐛 Bugs found** in seconds to minutes
- **💾 <100 KB** typical corpus size
- **⚡ 5 minutes** to set up and start fuzzing

---

## 🎪 Demo Hub

**[📚 Visit the Complete Demo Hub →](demo/README.md)**

The demo hub contains:
- 🎨 Beautiful visual architecture diagrams
- 💡 5 complete security vulnerability examples
- 🎓 4 comprehensive tutorials
- 🔧 3 integration templates (Maven, Gradle, GitHub Actions)
- 📊 Performance comparisons and benchmarks

---

## 🚀 Get Started Right Now

### Option 1: Try an Example (2 minutes)

```bash
cd demo/examples/sql-injection-demo
JAZZER_FUZZ=1 mvn test
# Watch Jazzer find SQL injection automatically!
```

### Option 2: Add to Your Project (5 minutes)

**Maven:**
```xml
<dependency>
    <groupId>com.code-intelligence</groupId>
    <artifactId>jazzer-junit</artifactId>
    <version>0.24.0</version>
    <scope>test</scope>
</dependency>
```

**Your First Fuzz Test:**
```java
import com.code_intelligence.jazzer.junit.FuzzTest;

class MyFuzzTest {
    @FuzzTest
    void fuzzMyParser(String input) {
        MyParser.parse(input);
    }
}
```

**Run It:**
```bash
JAZZER_FUZZ=1 mvn test
```

[Full Quick Start Guide →](demo/tutorials/quickstart.md)

---

## 📚 Documentation Quick Links

- 🏠 [Main README](README.md) - Project overview
- 📖 [Arguments & Configuration](docs/arguments-and-configuration-options.md)
- 🔬 [Mutation Framework](docs/mutation-framework.md)
- 🚀 [Advanced Techniques](docs/advanced.md)
- 🏗️ [JUnit Implementation](docs/dev-junit-implementation-details.md)

---

## 🤝 Community

- 💬 [GitHub Discussions](https://github.com/CodeIntelligenceTesting/jazzer/discussions)
- 🐛 [Issue Tracker](https://github.com/CodeIntelligenceTesting/jazzer/issues)
- 🐦 [Twitter @CI_Fuzz](https://twitter.com/CI_Fuzz)
- 📧 [Mailing List](https://groups.google.com/g/jazzer-users)

---

## 🎁 What Makes This Demo Special?

✨ **Comprehensive** - Everything from basics to advanced topics  
✨ **Interactive** - Working code you can run immediately  
✨ **Beautiful** - Well-organized with visual diagrams  
✨ **Practical** - Real security vulnerabilities, not toy examples  
✨ **Production-Ready** - Copy-paste templates for your projects  
✨ **Educational** - Clear explanations and best practices  

---

## 🎬 Next Actions

1. ✅ **[Visit the Demo Hub](demo/README.md)** - Start exploring
2. ✅ **[Try an Example](demo/examples/)** - See fuzzing in action
3. ✅ **[Follow a Tutorial](demo/tutorials/)** - Learn the fundamentals
4. ✅ **[Add to Your Project](demo/integrations/)** - Start fuzzing your code
5. ✅ **[Share Your Findings](docs/trophies.md)** - Contribute to the community!

---

<div align="center">

## 🎉 Ready to Find Bugs?

**[🚀 Start with the 5-Minute Quick Start →](demo/tutorials/quickstart.md)**

---

Made with ❤️ by [Code Intelligence](https://code-intelligence.com)

**Happy Fuzzing!** 🎯

[⬆ Back to Top](#-demomd---jazzer-interactive-demo-showcase)

</div>
