package com.example;

import com.code_intelligence.jazzer.junit.FuzzTest;
import com.code_intelligence.jazzer.mutation.annotation.NotNull;
import com.code_intelligence.jazzer.mutation.annotation.WithUtf8Length;

import java.sql.SQLException;

/**
 * Fuzz test for the SAFE version using prepared statements.
 * This should not find any SQL injection vulnerabilities.
 */
class SafeSqlFuzzTest {
    
    @FuzzTest(maxDuration = "30s")
    void fuzzSafeAuth(
        @NotNull @WithUtf8Length(max = 100) String username,
        @NotNull @WithUtf8Length(max = 100) String password
    ) {
        try {
            SafeUserDatabase db = new SafeUserDatabase();
            
            // This uses prepared statements - safe from SQL injection
            db.authenticateSafe(username, password);
            
            db.close();
        } catch (SQLException e) {
            // Expected for database errors, but NOT for SQL injection
        }
    }
    
    @FuzzTest(maxDuration = "30s")
    void fuzzSafeSearch(@NotNull @WithUtf8Length(max = 100) String searchTerm) {
        try {
            SafeUserDatabase db = new SafeUserDatabase();
            db.searchUsers(searchTerm);
            db.close();
        } catch (SQLException e) {
            // Expected for database errors
        }
    }
}
