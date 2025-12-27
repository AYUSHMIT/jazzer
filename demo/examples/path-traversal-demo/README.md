# 📂 Path Traversal Detection Demo

<div align="center">

**Find Directory Escape Vulnerabilities with Fuzzing**

[🏠 Demo Hub](../../README.md) | [💡 All Examples](../)

</div>

---

## 🎯 What This Demo Shows

This example demonstrates **Path Traversal** (directory escape) vulnerability detection:

- ✅ Vulnerable file access patterns
- ✅ Safe path validation
- ✅ Automatic detection by Jazzer's sanitizer
- ✅ Real attack patterns

**Difficulty:** ⭐ Beginner

---

## 🔍 What is Path Traversal?

Path Traversal allows attackers to access files outside intended directories using special sequences like `../` or absolute paths.

### Example Attack

```
# Normal: /var/app/uploads/photo.jpg
# Attack:  /var/app/uploads/../../etc/passwd
# Result:  /etc/passwd (outside allowed directory!)
```

---

## 💻 Vulnerable Pattern

```java
public byte[] readFile(String filename) {
    // VULNERABLE: No validation
    File file = new File("/var/app/uploads/" + filename);
    return Files.readAllBytes(file.toPath());
}

// Attack: filename = "../../etc/passwd"
// Reads: /etc/passwd ⚠️
```

---

## ✅ Safe Pattern

```java
public byte[] readFileSafe(String filename) {
    Path uploadDir = Paths.get("/var/app/uploads");
    Path requestedPath = uploadDir.resolve(filename).normalize();
    
    // Validate path stays within allowed directory
    if (!requestedPath.startsWith(uploadDir)) {
        throw new SecurityException("Path traversal detected");
    }
    
    return Files.readAllBytes(requestedPath);
}
```

---

## 🚀 Quick Run

```bash
cd demo/examples/path-traversal-demo

# See Jazzer find path traversal bugs
JAZZER_FUZZ=1 mvn test -Dtest=PathTraversalFuzzTest
```

---

## 🎯 Attack Patterns Jazzer Discovers

- `../` - Parent directory escape
- `..\\` - Windows path escape
- `/etc/passwd` - Absolute path
- `....//` - Double encoding
- `%2e%2e%2f` - URL encoded
- Symlink exploits

---

## 🛡️ Configuration

```java
import com.code_intelligence.jazzer.api.BugDetectors;

// Configure allowed directories
BugDetectors.allowFileSystemOperations((path) -> {
    return path.startsWith("/var/app/uploads");
});
```

---

## 📚 Learn More

- 🛡️ [Sanitizer Configuration Guide](../../tutorials/sanitizer-guide.md)
- 🚀 [Advanced Techniques](../../tutorials/advanced-techniques.md)
- 🏠 [Demo Hub](../../README.md)

---

<div align="center">

**Secure Your File Operations! 📂**

[🏠 Demo Hub](../../README.md)

</div>
