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
