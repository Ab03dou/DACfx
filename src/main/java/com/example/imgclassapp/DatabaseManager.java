package com.example.imgclassapp;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "dacBD";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "0100mysql0445";

    public Connection connectToDatabase(String IMAGE_DIRECTORY) {
        try {
            // First, connect to the MySQL server
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Check if the database exists
            ResultSet resultSet = conn.getMetaData().getCatalogs();
            boolean databaseExists = false;

            while (resultSet.next()) {
                String existingDbName = resultSet.getString(1);
                if (existingDbName.equals(DB_NAME)) {
                    databaseExists = true;
                    break;
                }
            }
            resultSet.close();

            // If database doesn't exist, create it
            if (!databaseExists) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("CREATE DATABASE " + DB_NAME);

                    System.out.println("Database " + DB_NAME + " created successfully.");
                }
                String createTableSQL = "CREATE TABLE IF NOT EXISTS " + DB_NAME + "." + IMAGE_DIRECTORY + " (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "filePath VARCHAR(255) NOT NULL)";
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createTableSQL);
                    System.out.println("Table 'images' verified/created.");
                } catch (SQLException e) {
                    System.err.println("Failed to create table: " + e.getMessage());
                }
            }

            // Close the initial connection and reconnect to the specific database
            conn.close();
            conn = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASSWORD);
            return conn;
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return null;
        }
    }


    public void saveImagePath(Connection conn, String imagePath) {
        if (conn == null) {
            System.err.println("No connection to the database.");
            return;
        }

        String insertSQL = "INSERT INTO images (filePath) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, imagePath);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Image path saved to database: " + imagePath);
            }
        } catch (SQLException e) {
            System.err.println("Failed to insert image path: " + e.getMessage());
        }
    }

    public void deleteImagePath(Connection conn,File file) {

        // Remove the image path from the database
        String deleteSQL = "DELETE FROM images WHERE filePath = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setString(1, file.getAbsolutePath());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Image path deleted from database: " + file.getAbsolutePath());
            }
        } catch (SQLException e) {
            System.err.println("Failed to delete image path from database: " + e.getMessage());
        }
    }

    public List<File> getFilesForClassification(String classification, Connection connection) throws SQLException {
        List<File> files = new ArrayList<>();

        String query = classification.equals("All")
                ? "SELECT filePath FROM images"
                : "SELECT filePath FROM images WHERE filePath LIKE ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            if (!classification.equals("All")) {
                pstmt.setString(1, "%/" + classification + "/%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String filePath = rs.getString("filePath");
                    File file = new File(filePath);

                    // Optional: Add only image files with specific extensions
                    if (file.exists() && (filePath.toLowerCase().endsWith(".jpg") ||
                            filePath.toLowerCase().endsWith(".png") ||
                            filePath.toLowerCase().endsWith(".jpeg"))) {
                        files.add(file);
                    }
                }
            }
        }

        return files;
    }
}
