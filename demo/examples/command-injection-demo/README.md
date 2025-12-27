# ⚡ Command Injection Detection Demo

<div align="center">

**Find OS Command Injection Vulnerabilities**

[🏠 Demo Hub](../../README.md) | [💡 All Examples](../)

</div>

---

## 🎯 What This Demo Shows

This example demonstrates **Command Injection** vulnerability detection:

- ✅ Vulnerable command execution
- ✅ Safe command patterns
- ✅ Automatic detection by Jazzer's sanitizer
- ✅ Shell metacharacter attacks

**Difficulty:** ⭐⭐ Intermediate

---

## 🔍 What is Command Injection?

Command Injection allows attackers to execute arbitrary OS commands by injecting shell metacharacters into user input.

### Example Attack

```bash
# Normal:  ping -c 1 example.com
# Attack:  ping -c 1 example.com; cat /etc/passwd
# Result:  Executes both commands! ⚠️
```

---

## 💻 Vulnerable Patterns

```java
// VULNERABLE: Shell command with user input
public void pingHost(String hostname) {
    String cmd = "ping -c 1 " + hostname;
    Runtime.getRuntime().exec(cmd);
}

// Attack: hostname = "example.com; rm -rf /"
// Executes: ping -c 1 example.com; rm -rf / ⚠️
```

```java
// ALSO VULNERABLE: Array with shell
public void checkLog(String logfile) {
    String[] cmd = {"/bin/sh", "-c", "cat /var/log/" + logfile};
    Runtime.getRuntime().exec(cmd);
}

// Attack: logfile = "app.log; cat /etc/passwd"
// Executes: cat /var/log/app.log; cat /etc/passwd ⚠️
```

---

## ✅ Safe Patterns

### Safe: Array Form, No Shell

```java
public void pingHostSafe(String hostname) {
    // Validate input first
    if (!hostname.matches("[a-zA-Z0-9.-]+")) {
        throw new IllegalArgumentException("Invalid hostname");
    }
    
    // Use array form - no shell interpretation
    String[] cmd = {"ping", "-c", "1", hostname};
    Runtime.getRuntime().exec(cmd);
    
    // Semicolons treated as part of hostname, not command separator
}
```

### Best: ProcessBuilder

```java
public void checkLogSafe(String logfile) {
    // Validate filename
    if (!logfile.matches("[a-zA-Z0-9._-]+\\.log")) {
        throw new IllegalArgumentException("Invalid log file");
    }
    
    // ProcessBuilder doesn't invoke shell
    ProcessBuilder pb = new ProcessBuilder(
        "cat",
        "/var/log/" + logfile
    );
    pb.start();
}
```

---

## 🚀 Quick Run

```bash
cd demo/examples/command-injection-demo

# See Jazzer detect command injection
JAZZER_FUZZ=1 mvn test -Dtest=CommandInjectionFuzzTest
```

---

## 🎯 Attack Patterns Jazzer Discovers

- `;` - Command separator
- `|` - Pipe to another command
- `&` - Background execution
- `&&` - Conditional execution
- `||` - Or execution
- `$()` - Command substitution
- `` ` `` - Backtick substitution
- `\n` - Newline injection

---

## 🛡️ Defense Checklist

- ✅ Validate input with strict allowlist
- ✅ Use array form of exec()
- ✅ Never invoke shell (`/bin/sh`, `cmd.exe`)
- ✅ Use ProcessBuilder for complex cases
- ✅ Avoid user input in commands entirely if possible
- ✅ Run with minimum privileges
- ✅ Use security manager restrictions

---

## 📚 Learn More

- 🛡️ [Sanitizer Configuration Guide](../../tutorials/sanitizer-guide.md)
- 🚀 [Advanced Techniques](../../tutorials/advanced-techniques.md)
- 🏠 [Demo Hub](../../README.md)

---

<div align="center">

**Secure Your Command Execution! ⚡**

[🏠 Demo Hub](../../README.md)

</div>
