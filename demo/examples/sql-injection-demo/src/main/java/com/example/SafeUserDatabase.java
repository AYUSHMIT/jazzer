package com.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SAFE: This class demonstrates proper SQL injection prevention
 * using prepared statements with parameter binding.
 */
public class SafeUserDatabase {
    
    private Connection connection;
    
    public SafeUserDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:safedb");
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
     * SAFE: Using prepared statements with parameter binding
     */
    public User authenticateSafe(String username, String password) throws SQLException {
        // ✅ SAFE: Parameters are properly escaped
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, username);  // Parameter binding prevents injection
        stmt.setString(2, password);
        
        ResultSet rs = stmt.executeQuery();
        
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
