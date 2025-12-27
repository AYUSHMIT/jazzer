# 🔓 Deserialization Vulnerability Detection Demo

<div align="center">

**Find Unsafe Java Deserialization Issues**

[🏠 Demo Hub](../../README.md) | [💡 All Examples](../)

</div>

---

## 🎯 What This Demo Shows

This example demonstrates **Deserialization** vulnerability detection:

- ✅ Unsafe deserialization patterns
- ✅ Gadget chain detection
- ✅ Automatic detection by Jazzer's sanitizer
- ✅ Safe deserialization with allowlists

**Difficulty:** ⭐⭐⭐ Advanced

---

## 🔍 What is Unsafe Deserialization?

Java deserialization can execute arbitrary code if an attacker provides a crafted serialized object containing malicious "gadget chains."

### The Attack

```java
// Attacker creates malicious serialized object
// Object triggers code execution during deserialization
// Can lead to remote code execution (RCE)!
```

---

## 💻 Vulnerable Pattern

```java
public Object deserialize(byte[] data) {
    // VULNERABLE: Deserializing untrusted data
    ByteArrayInputStream bis = new ByteArrayInputStream(data);
    ObjectInputStream ois = new ObjectInputStream(bis);
    return ois.readObject();  // ⚠️ Can execute arbitrary code!
}
```

---

## ✅ Safe Pattern with Allowlist

```java
public Object deserializeSafe(byte[] data) {
    ByteArrayInputStream bis = new ByteArrayInputStream(data);
    
    // Safe: Custom ObjectInputStream with class allowlist
    ObjectInputStream ois = new ObjectInputStream(bis) {
        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc)
                throws IOException, ClassNotFoundException {
            
            // Only allow specific safe classes
            String className = desc.getName();
            if (className.equals("com.example.SafeData") ||
                className.equals("com.example.UserProfile") ||
                className.equals("java.lang.String") ||
                className.equals("java.util.ArrayList")) {
                return super.resolveClass(desc);
            }
            
            // Block everything else
            throw new InvalidClassException(
                "Unauthorized deserialization attempt: " + className
            );
        }
    };
    
    return ois.readObject();
}
```

---

## 🚀 Quick Run

```bash
cd demo/examples/deserialization-demo

# See Jazzer detect unsafe deserialization
JAZZER_FUZZ=1 mvn test -Dtest=DeserializationFuzzTest
```

---

## 🎯 What Jazzer Detects

- Deserialization of untrusted data
- Known dangerous classes (gadget chains)
- Suspicious class loading during deserialization
- Potential RCE (Remote Code Execution) attempts

### Known Dangerous Classes

- `org.apache.commons.collections.functors.InvokerTransformer`
- `com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl`
- `org.springframework.beans.factory.ObjectFactory`
- Many others in common libraries

---

## 🛡️ Best Practices

### 1. Avoid Deserialization Entirely

```java
// Use JSON, Protocol Buffers, or other formats
String json = "...";
MyObject obj = new Gson().fromJson(json, MyObject.class);
```

### 2. Use Class Allowlists

```java
// Only allow specific safe classes
ObjectInputStream ois = new ValidatingObjectInputStream(data, allowedClasses);
```

### 3. Sign Serialized Data

```java
// Verify integrity before deserializing
if (!verifySignature(data, signature)) {
    throw new SecurityException("Invalid signature");
}
```

### 4. Use Security Manager

```java
// Restrict what deserialization can do
System.setSecurityManager(new SecurityManager());
```

---

## 🔥 Famous CVEs

Deserialization bugs have caused major vulnerabilities:

- **CVE-2015-4852** - Oracle WebLogic RCE
- **CVE-2017-3066** - Adobe ColdFusion RCE
- **CVE-2017-12149** - JBoss AS RCE
- **CVE-2018-1000873** - Jenkins RCE

---

## 📚 Learn More

- 🛡️ [Sanitizer Configuration Guide](../../tutorials/sanitizer-guide.md)
- 📖 [Java Deserialization Security](https://owasp.org/www-community/vulnerabilities/Deserialization_of_untrusted_data)
- 🏠 [Demo Hub](../../README.md)

---

<div align="center">

**Secure Your Deserialization! 🔓**

[🏠 Demo Hub](../../README.md)

</div>
