# 🗄️ SQL Injection Detection Demo

<div align="center">

**Discover SQL Injection Vulnerabilities with Fuzzing**

[🏠 Demo Hub](../../README.md) | [💡 All Examples](../)

</div>

---

## 🎯 What This Demo Shows

This example demonstrates how Jazzer automatically detects **SQL injection vulnerabilities** through fuzzing:

- ✅ Vulnerable code patterns
- ✅ Safe code patterns
- ✅ Automatic detection by sanitizer
- ✅ Real malicious inputs that trigger detection

**Time to Run:** 2-5 minutes  
**Difficulty:** ⭐⭐ Intermediate

---

## 🔍 What is SQL Injection?

SQL Injection is a security vulnerability that allows attackers to manipulate database queries by injecting malicious SQL code through user input.

### Example Attack

```sql
-- Normal query
SELECT * FROM users WHERE username = 'alice' AND password = 'secret123'

-- Injected query (input: admin' OR '1'='1' --)
SELECT * FROM users WHERE username = 'admin' OR '1'='1' --' AND password = 'anything'
```

The attacker bypasses authentication!

---

## 📁 Project Structure

```
sql-injection-demo/
├── README.md (this file)
├── pom.xml
└── src/
    ├── main/java/com/example/
    │   ├── UserDatabase.java          # Vulnerable database class
    │   └── SafeUserDatabase.java      # Fixed version
    └── test/java/com/example/
        ├── SqlInjectionFuzzTest.java  # Fuzz test
        └── SafeSqlFuzzTest.java       # Safe version test
```

---

## 🚀 Quick Start

### 1. Run the Demo

```bash
cd demo/examples/sql-injection-demo

# Run the vulnerable version (will find bugs!)
JAZZER_FUZZ=1 mvn test -Dtest=SqlInjectionFuzzTest

# Run the safe version (no bugs)
mvn test -Dtest=SafeSqlFuzzTest
```

### 2. Expected Output

**Vulnerable Version:**
```
== Java Exception: com.code_intelligence.jazzer.api.FuzzerSecurityIssueHigh: SQL Injection
Injected query: SELECT * FROM users WHERE username = '' OR '1'='1' --'

artifact_prefix='./'; Test unit written to crash-sqlinjection
```

**Safe Version:**
```
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
✅ No vulnerabilities found!
```

---

## 💻 The Code

### Vulnerable Version

`src/main/java/com/example/UserDatabase.java`:

```java
package com.example;

import java.sql.*;

/**
 * VULNERABLE: This class demonstrates SQL injection vulnerability
 * by directly concatenating user input into SQL queries.
 */
public class UserDatabase {
    
    private Connection connection;
    
    public UserDatabase() throws SQLException {
        // In-memory H2 database for demo
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb");
        initDatabase();
    }
    
    private void initDatabase() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("CREATE TABLE users (id INT, username VARCHAR(255), password VARCHAR(255))");
        stmt.execute("INSERT INTO users VALUES (1, 'admin', 'admin123')");
        stmt.execute("INSERT INTO users VALUES (2, 'user', 'user456')");
    }
    
    /**
     * VULNERABLE: Direct string concatenation allows SQL injection!
     */
    public User authenticateVulnerable(String username, String password) throws SQLException {
        // ⚠️ DANGER: User input directly in query
        String query = "SELECT * FROM users WHERE username = '" + username + 
                      "' AND password = '" + password + "'";
        
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        if (rs.next()) {
            return new User(rs.getInt("id"), 
                          rs.getString("username"), 
                          rs.getString("password"));
        }
        return null;
    }
    
    /**
     * VULNERABLE: Search with string concatenation
     */
    public List<User> searchUsers(String searchTerm) throws SQLException {
        // ⚠️ DANGER: Vulnerable to SQL injection
        String query = "SELECT * FROM users WHERE username LIKE '%" + searchTerm + "%'";
        
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        List<User> results = new ArrayList<>();
        while (rs.next()) {
            results.add(new User(rs.getInt("id"), 
                               rs.getString("username"), 
                               rs.getString("password")));
        }
        return results;
    }
    
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
```

### Safe Version

`src/main/java/com/example/SafeUserDatabase.java`:

```java
package com.example;

import java.sql.*;

/**
 * SAFE: This class demonstrates proper SQL injection prevention
 * using prepared statements with parameter binding.
 */
public class SafeUserDatabase {
    
    private Connection connection;
    
    public SafeUserDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb");
        initDatabase();
    }
    
    private void initDatabase() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("CREATE TABLE users (id INT, username VARCHAR(255), password VARCHAR(255))");
        stmt.execute("INSERT INTO users VALUES (1, 'admin', 'admin123')");
        stmt.execute("INSERT INTO users VALUES (2, 'user', 'user456')");
    }
    
    /**
     * SAFE: Using prepared statements with parameter binding
     */
    public User authenticateSafe(String username, String password) throws SQLException {
        // ✅ SAFE: Parameters are properly escaped
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, username);  // Parameter binding prevents injection
        stmt.setString(2, password);
        
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            return new User(rs.getInt("id"), 
                          rs.getString("username"), 
                          rs.getString("password"));
        }
        return null;
    }
    
    /**
     * SAFE: Search with prepared statement
     */
    public List<User> searchUsers(String searchTerm) throws SQLException {
        // ✅ SAFE: Using prepared statement
        String query = "SELECT * FROM users WHERE username LIKE ?";
        
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, "%" + searchTerm + "%");
        
        ResultSet rs = stmt.executeQuery();
        
        List<User> results = new ArrayList<>();
        while (rs.next()) {
            results.add(new User(rs.getInt("id"), 
                               rs.getString("username"), 
                               rs.getString("password")));
        }
        return results;
    }
    
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
```

### Fuzz Test

`src/test/java/com/example/SqlInjectionFuzzTest.java`:

```java
package com.example;

import com.code_intelligence.jazzer.junit.FuzzTest;
import com.code_intelligence.jazzer.mutation.annotation.NotNull;
import com.code_intelligence.jazzer.mutation.annotation.WithUtf8Length;

import java.sql.SQLException;

/**
 * Fuzz test that demonstrates SQL injection detection.
 * Jazzer's SQL Injection sanitizer will automatically detect vulnerabilities.
 */
class SqlInjectionFuzzTest {
    
    @FuzzTest(maxDuration = "30s")
    void fuzzVulnerableAuth(
        @NotNull @WithUtf8Length(max = 100) String username,
        @NotNull @WithUtf8Length(max = 100) String password
    ) {
        try {
            UserDatabase db = new UserDatabase();
            
            // Jazzer monitors this call
            // Will detect SQL injection when malformed SQL is executed
            db.authenticateVulnerable(username, password);
            
            db.close();
        } catch (SQLException e) {
            // Expected for invalid SQL
            // Sanitizer reports if SQL is malformed
        }
    }
    
    @FuzzTest(maxDuration = "30s")
    void fuzzVulnerableSearch(@NotNull @WithUtf8Length(max = 100) String searchTerm) {
        try {
            UserDatabase db = new UserDatabase();
            db.searchUsers(searchTerm);
            db.close();
        } catch (SQLException e) {
            // Expected for invalid SQL
        }
    }
}
```

---

## 🎯 Malicious Inputs That Trigger Detection

Here are examples of inputs that Jazzer will discover:

### Authentication Bypass

```
username: admin' OR '1'='1' --
password: anything
```

Resulting query:
```sql
SELECT * FROM users WHERE username = 'admin' OR '1'='1' --' AND password = 'anything'
```

### Data Extraction

```
username: ' UNION SELECT id, username, password FROM users --
password: anything
```

### Table Drop

```
username: '; DROP TABLE users; --
password: anything
```

---

## 📊 Running the Demo

### Step 1: Build the Project

```bash
cd demo/examples/sql-injection-demo
mvn clean compile
```

### Step 2: Run Fuzzing

```bash
# Fuzz the vulnerable version
JAZZER_FUZZ=1 mvn test -Dtest=SqlInjectionFuzzTest#fuzzVulnerableAuth

# Watch as Jazzer finds SQL injection!
```

### Step 3: Review Results

Check the crash inputs:
```bash
ls -la src/test/resources/com/example/SqlInjectionFuzzTestInputs/fuzzVulnerableAuth/
```

### Step 4: Test the Fix

```bash
# Test the safe version
mvn test -Dtest=SafeSqlFuzzTest

# No vulnerabilities found!
```

---

## 🎓 Key Takeaways

### ❌ Vulnerable Pattern

```java
// String concatenation = SQL Injection risk
String query = "SELECT * FROM users WHERE name = '" + userInput + "'";
statement.executeQuery(query);
```

### ✅ Safe Pattern

```java
// Prepared statements = Safe
String query = "SELECT * FROM users WHERE name = ?";
PreparedStatement stmt = connection.prepareStatement(query);
stmt.setString(1, userInput);
stmt.executeQuery();
```

### 🛡️ How Jazzer Helps

1. **Automatic Detection**: No manual test cases needed
2. **Coverage-Guided**: Explores different code paths
3. **Reproducible**: Saves crash inputs for regression testing
4. **Fast**: Finds bugs in seconds to minutes

---

## 🔧 Customization

### Test with Your Own Queries

Modify `UserDatabase.java` to test your own SQL patterns:

```java
public void customQuery(String input) throws SQLException {
    String query = "YOUR QUERY HERE " + input;
    statement.executeQuery(query);
}
```

Then add a fuzz test:

```java
@FuzzTest
void fuzzCustomQuery(String input) {
    // Test your custom code
}
```

---

## 📚 Further Reading

- 🛡️ [Sanitizer Configuration Guide](../../tutorials/sanitizer-guide.md)
- 🚀 [Advanced Techniques](../../tutorials/advanced-techniques.md)
- 💡 [More Examples](../)
- 🏠 [Demo Hub](../../README.md)

---

## 🆘 Troubleshooting

### "No tests were found"

Make sure the test class is not `private` and methods are annotated with `@FuzzTest`.

### "Sanitizer not detecting"

Ensure SQL exceptions are not caught and hidden:
```java
// BAD: Hides SQL injection
try {
    executeQuery(input);
} catch (Exception e) {
    // Swallows the error!
}

// GOOD: Let SQLException propagate
try {
    executeQuery(input);
} catch (SQLException e) {
    // OK - expected for invalid SQL
}
```

---

<div align="center">

**Secure Your Database Queries! 🗄️**

[⬆ Back to Top](#-sql-injection-detection-demo) | [🏠 Demo Hub](../../README.md)

</div>
