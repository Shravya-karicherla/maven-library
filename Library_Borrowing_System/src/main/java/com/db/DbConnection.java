package com.db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

public class DbConnection {

    private static Connection connection;

    // Private constructor to enforce Singleton pattern
    private DbConnection() {}

    // Static method to get DB connection instance
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "root");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    // Method to close connection
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close(); 
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

