package com.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        stmt.close();
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
        
        User user = null;
        if (rs.next()) {
            user = new User(rs.getInt("id"), 
                          rs.getString("username"), 
                          rs.getString("password"));
        }
        rs.close();
        stmt.close();
        return user;
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
        rs.close();
        stmt.close();
        return results;
    }
    
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
