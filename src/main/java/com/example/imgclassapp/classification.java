package com.example.imgclassapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.List;

public class classification extends Application {


    private static final String IMAGE_DIRECTORY = "images";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "0100mysql0445";

    private VBox classButtonsArea;
    private HBox contentArea;

    @Override
    public void start(Stage primaryStage) {

        // Main container using HBox for left-right split
        HBox root = new HBox(20);
        root.getStyleClass().add("main-container");

        // Left section for classifications (will now appear on the right)
        VBox leftSection = new VBox(15);
        leftSection.getStyleClass().add("left-section");

        classButtonsArea = new VBox(10);
        classButtonsArea.getStyleClass().add("class-buttons-area");

        for (int i = 1; i <= 5; i++) {

            contentArea = new HBox();
            contentArea.getStyleClass().add("content-area");
            classButtonsArea.getChildren().add(contentArea);
        }

        leftSection.getChildren().addAll(classButtonsArea);

        // Right section for upload (will now appear on the left)
        VBox rightSection = new VBox(20);
        rightSection.getStyleClass().add("right-section");

        Label welcomeLabel = new Label("Transform Your Images with AI");
        welcomeLabel.getStyleClass().add("welcome-label");

        Label subLabel = new Label("Drop your images below and let our AI classify them instantly");
        subLabel.getStyleClass().add("sub-label");

        VBox uploadArea = new VBox(15);
        uploadArea.getStyleClass().add("upload-area");

        Label uploadLabel = new Label("Drop Files Here");
        uploadLabel.getStyleClass().add("upload-label");

        Button addButton = new Button("Choose Files");
        addButton.getStyleClass().add("add-button");

        uploadArea.getChildren().addAll(uploadLabel, addButton);
        rightSection.getChildren().addAll(welcomeLabel, subLabel, uploadArea);
        // Adjust layout properties for the sections
        HBox.setHgrow(leftSection, Priority.ALWAYS); // Left section takes more space
        HBox.setHgrow(rightSection, Priority.NEVER); // Right section does not expand

        leftSection.setPrefWidth(700); // Increase width of the left section
        rightSection.setPrefWidth(400); // Adjust width for the right section
        // Add sections to root with reversed order
        root.getChildren().addAll(rightSection, leftSection);

        //button func

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        addButton.setOnAction(event -> {
            List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);

            if (files != null) {
//                tilePane.getChildren().clear();
                try (Connection conn = connectToDB()) {
                    for (File file : files) {
                        File savedFile = saveFileToProjectFolder(file);
                        if (savedFile != null) {
                            String imagePath = savedFile.getAbsolutePath();
                            saveImagePathToDB(conn, imagePath);

                            Image image = new Image(new FileInputStream(savedFile));
                            CustomImageView imageView = new CustomImageView(image, imagePath);
                            imageView.setFitWidth(150);
                            imageView.setFitHeight(150);
                            imageView.setPreserveRatio(true);

                            contentArea.getChildren().add(imageView);
                        }
                    }
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        // Create scene
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/styles/classification.css").toExternalForm());

        // Configure stage
        primaryStage.setTitle("Image Classification");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Connection connectToDB() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to the database successfully.");
            return conn;
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return null;
        }
    }

    private File saveFileToProjectFolder(File originalFile) throws IOException {
        File directory = new File(IMAGE_DIRECTORY);

        if (!directory.exists()) {
            Files.createDirectories(directory.toPath());
        }

        File destFile = new File(directory, originalFile.getName());
        try (FileInputStream inputStream = new FileInputStream(originalFile);
             FileOutputStream outputStream = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }

        return destFile;
    }

    private void saveImagePathToDB(Connection conn, String imagePath) {
        if (conn == null) {
            System.err.println("No connection to the database.");
            return;
        }

        String createTableSQL = "CREATE TABLE IF NOT EXISTS images (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "filePath VARCHAR(255) NOT NULL)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Table 'images' verified/created.");
        } catch (SQLException e) {
            System.err.println("Failed to create table: " + e.getMessage());
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

}
