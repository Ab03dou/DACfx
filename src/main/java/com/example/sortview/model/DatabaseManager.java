package com.example.sortview.model;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "sortview";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String IMAGE_DIRECTORY = "images";
    private static final Logger logger = LoggerFactory.getLogger(Connection.class);



    public Connection connectToDatabase() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

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

            if (!databaseExists) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("CREATE DATABASE " + DB_NAME);

                    logger.info("Database " + DB_NAME + " created successfully.");
                }
                String createTableSQLForIMAGE_DIRECTORY = "CREATE TABLE IF NOT EXISTS " + DB_NAME + "." + IMAGE_DIRECTORY + " (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "filePath VARCHAR(255) NOT NULL UNIQUE, " +
                        "className VARCHAR(40) NOT NULL, " +
                        "confidence DOUBLE NOT NULL)";
                String createTableSQLForImageClass = "CREATE TABLE IF NOT EXISTS " + DB_NAME + ".className" + " (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "className VARCHAR(40) NOT NULL)";
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createTableSQLForIMAGE_DIRECTORY);
                    System.out.println("Table 'images' verified/created.");
                    stmt.execute(createTableSQLForImageClass);
                    System.out.println("Table 'className' verified/created.");
                } catch (SQLException e) {
                    System.err.println("Failed to create table: " + e.getMessage());
                }
            }

            conn.close();
            conn = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASSWORD);
            return conn;
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return null;
        }
    }


    public void saveImagePath(Connection conn, String imagePath, String classification, double confidence) {
        if (conn == null) {
            System.err.println("No connection to the database.");
            return;
        }

        String insertSQL = "INSERT IGNORE INTO images (filePath, className, confidence) VALUES (?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, imagePath);
            pstmt.setString(2, classification);
            pstmt.setDouble(3, confidence);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("saved to database image:" + imagePath + " confidence: " + confidence);
            }
        } catch (SQLException e) {
            System.err.println("Failed to insert image path: " + e.getMessage());
        }
    }

    public void saveCLassName(Connection conn, String className) {
        if (conn == null) {
            System.err.println("No connection to the database.");
            return;
        }

        String insertSQL = "INSERT IGNORE INTO "+DB_NAME+".className (className) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, className);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("saved to className database:" + className);
            }
        } catch (SQLException e) {
            System.err.println("Failed to insert className: " + e.getMessage());
        }
    }

    public List<File> getFilesForClassification(String classification, Connection connection) throws SQLException {
        List<File> files = new ArrayList<>();

        String query = classification.equals("All")
                ? "SELECT filePath FROM images ORDER BY confidence DESC"
                : "SELECT filePath,confidence FROM images WHERE className LIKE ? ORDER BY confidence DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            if (!classification.equals("All")) {
                pstmt.setString(1,  classification);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String filePath = rs.getString("filePath");
                    File file = new File(filePath);

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

    public List<String> getClassesNamesNew(Connection conn) {
        String getClasses = "SELECT DISTINCT className FROM "+DB_NAME+".images";

        List<String> classList = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(getClasses);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                classList.add(rs.getString("className"));
            }
        } catch (SQLException e) {
            System.err.println("Failed to retrieve class names: " + e.getMessage());
        }
        classList.addFirst("All");

        return classList;
    }

    public List<String> getClassesNamesOld(Connection conn) {
        String getClasses = "SELECT DISTINCT className FROM "+DB_NAME+".className";
        List<String> classList = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(getClasses);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                classList.add(rs.getString("className"));
            }
        } catch (SQLException e) {
            System.err.println("Failed to retrieve class names: " + e.getMessage());
        }
        classList.addFirst("All");

        return classList;
    }
}
