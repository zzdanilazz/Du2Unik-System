package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/du2unikdb";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "0000";

    private Connection connection;

    public void connect() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            connection.setAutoCommit(true);
//            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
//                System.out.println("Disconnected from the database.");
            } catch (SQLException e) {
                System.out.println("Failed to disconnect from the database.");
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection(){
        return connection;
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

// Добавьте другие методы для выполнения операций с базой данных.
}
