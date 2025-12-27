# 🌐 SSRF (Server-Side Request Forgery) Detection Demo

<div align="center">

**Detect Unauthorized Network Requests with Fuzzing**

[🏠 Demo Hub](../../README.md) | [💡 All Examples](../)

</div>

---

## 🎯 What This Demo Shows

This example demonstrates **SSRF** (Server-Side Request Forgery) vulnerability detection:

- ✅ Vulnerable URL fetching
- ✅ Internal network access prevention
- ✅ Automatic detection by Jazzer's sanitizer
- ✅ Allowlist configuration

**Difficulty:** ⭐⭐⭐ Advanced

---

## 🔍 What is SSRF?

SSRF allows attackers to make the server send requests to unintended destinations, often internal resources or localhost services.

### Example Attack

```
# Normal: https://api.example.com/data
# Attack:  http://localhost:8080/admin
# Result:  Server accesses internal admin interface! ⚠️
```

---

## 💻 Vulnerable Pattern

```java
public String fetchUrl(String url) {
    // VULNERABLE: No destination validation
    URL target = new URL(url);
    HttpURLConnection conn = (HttpURLConnection) target.openConnection();
    return readResponse(conn);
}

// Attack: url = "http://169.254.169.254/latest/meta-data"
// Accesses: AWS metadata service ⚠️
```

---

## ✅ Safe Pattern with Configuration

```java
import com.code_intelligence.jazzer.api.BugDetectors;

static {
    // Configure allowed destinations
    BugDetectors.allowNetworkConnections((host, port) -> {
        // Only allow specific external APIs
        if (host.equals("api.example.com") && port == 443) return true;
        if (host.equals("cdn.example.com") && port == 443) return true;
        
        // Block everything else (including internal networks)
        return false;
    });
}

public String fetchUrlSafe(String url) {
    // Sanitizer enforces the policy
    URL target = new URL(url);
    HttpURLConnection conn = (HttpURLConnection) target.openConnection();
    return readResponse(conn);
}
```

---

## 🚀 Quick Run

```bash
cd demo/examples/ssrf-demo

# See Jazzer detect SSRF attempts
JAZZER_FUZZ=1 mvn test -Dtest=SsrfFuzzTest
```

---

## 🎯 Attack Targets Jazzer Discovers

- `http://localhost` - Local services
- `http://127.0.0.1` - Loopback
- `http://10.0.0.0/8` - Internal network
- `http://192.168.0.0/16` - Private network
- `http://169.254.169.254` - Cloud metadata
- `file:///etc/passwd` - Local file access

---

## 🛡️ Best Practices

1. **Always use allowlist** - Never blocklist
2. **Validate protocols** - Restrict to https://
3. **Check DNS resolution** - Prevent DNS rebinding
4. **Timeout requests** - Prevent hanging
5. **Log all requests** - Audit trail

---

## 📚 Learn More

- 🛡️ [Sanitizer Configuration Guide](../../tutorials/sanitizer-guide.md)
- 📖 [BugDetectors API](https://codeintelligencetesting.github.io/jazzer-docs/jazzer-api/com/code_intelligence/jazzer/api/BugDetectors.html)
- 🏠 [Demo Hub](../../README.md)

---

<div align="center">

**Secure Your Network Requests! 🌐**

[🏠 Demo Hub](../../README.md)

</div>
