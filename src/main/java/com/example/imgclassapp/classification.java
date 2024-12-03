package com.example.imgclassapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class classification extends Application {

    private static final String IMAGE_DIRECTORY = "images";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "dacBD";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "0100mysql0445";

    private static final String[] CLASSIFICATIONS = {"All", "Document", "People", "Animals", "Nature", "Screenshot",
            "Other"};

    private TilePane classesArea;

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {

        HBox root = new HBox(100);
        root.getStyleClass().add("main-container");

        VBox rightSection = new VBox();
        rightSection.getStyleClass().add("right-section");

        classesArea = new TilePane();
        classesArea.getStyleClass().add("classes-area");

        for (int i = 0; i < CLASSIFICATIONS.length; i++) {
            GridPane classCn = new GridPane();
            classCn.getStyleClass().add("classCn-area");
            classCn.setHgap(10);
            classCn.setVgap(10);

            Label classCnName = new Label(CLASSIFICATIONS[i]);
            classCnName.getStyleClass().add("image-name-label");

            VBox contactClassCn = new VBox(5); // 5 is the spacing between elements
            contactClassCn.getChildren().addAll(classCn, classCnName);

            classesArea.getChildren().add(contactClassCn);

            final int index = i;
            contactClassCn.setOnMouseClicked(event -> {
                try {

                    try (Connection conn = connectToDB()) {
                        showImageDisplayPage(getFilesForClassification(CLASSIFICATIONS[index], conn));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }

        showIMagesInClasses();

        rightSection.getChildren().addAll(classesArea);

        VBox leftSection = new VBox(20);
        leftSection.getStyleClass().add("left-section");

        VBox uploadArea = new VBox(15);
        uploadArea.getStyleClass().add("upload-area");

        Label uploadLabel = new Label("Classify Your Images with AI");
        uploadLabel.getStyleClass().add("upload-label");

        Label subLabel = new Label("Select your images below and let our AI classify them instantly");
        subLabel.getStyleClass().add("sub-label");

        Button addButton = new Button("Choose Files");
        addButton.getStyleClass().add("add-button");

        uploadArea.getChildren().addAll(uploadLabel, subLabel, addButton);

        VBox resVBOX = new VBox();
        resVBOX.getStyleClass().add("res-VBOX");
        ScrollPane s = new ScrollPane(resVBOX);
        s.getStyleClass().add("res-SCROLL");

        s.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        s.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leftSection.getChildren().addAll(uploadArea, s);

        root.getChildren().addAll(leftSection, rightSection);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        addButton.setOnAction(event -> {
            List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
            if (files != null) {
                for (File file : files) {
                    try {
                        // TODO: fi next sprint nbdlo fl code hada bah iwli ikhyr class bl ai mch tji
                        // kima dok
                        int classNum = 1;
                        saveFileToProjectFolder(file, CLASSIFICATIONS[classNum]);
                        showIMagesInResClasses(resVBOX, file, classNum);
                        showIMagesInClasses();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Scene scene = new Scene(root, 1380, 700);
        scene.getStylesheets().add(getClass().getResource("/styles/classification.css").toExternalForm());

        primaryStage.setTitle("Image Classification");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private List<File> getFilesForClassification(String classification, Connection connection) throws SQLException {
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

    private void showIMagesInResClasses(VBox h, File imageFile, int r) throws FileNotFoundException {

        Image image = new Image(new FileInputStream(imageFile));
        String imagePath = imageFile.getAbsolutePath();
        CustomImageView imageView = new CustomImageView(image, imagePath);
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        HBox b = new HBox();
        Label l = new Label("Classified as: " + CLASSIFICATIONS[r]);
        b.getChildren().addAll(imageView, l);
        h.getChildren().add(b);
        b.getStyleClass().add("res-class-area");
    }

    private void showIMagesInClasses() throws FileNotFoundException {
        for (int j = 0; j < CLASSIFICATIONS.length; j++) {
            List<File> files = null;
            try (Connection conn = connectToDB()) {
                files = getFilesForClassification(CLASSIFICATIONS[j], conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            displayImagesFromDirectory(files, j);
        }
    }

    private void displayImagesFromDirectory(List<File> files, int classificationIndex) throws FileNotFoundException {

        if (files != null && files.size() > 0) {
            for (int i = 0; i < Math.min(files.size(), 4); i++) {
                File imageFile = files.get(i);
                Image image = new Image(new FileInputStream(imageFile));
                String imagePath = imageFile.getAbsolutePath();
                CustomImageView imageView = new CustomImageView(image, imagePath);
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
                VBox vBox = (VBox) classesArea.getChildren().get(classificationIndex);
                GridPane classCn = (GridPane) vBox.getChildren().get(0);
                classCn.add(imageView, i % 2, i / 2);
            }
        }
    }

    private void saveFileToProjectFolder(File originalFile, String dir) throws IOException {

        File directory = new File(IMAGE_DIRECTORY + "/" + dir);

        if (!directory.exists()) {
            Files.createDirectories(directory.toPath());
        }

        File destFile = new File(directory, originalFile.getName());
        copyImage(originalFile.getAbsolutePath(),destFile.getAbsolutePath());
        try (Connection conn = connectToDB()) {
            saveImagePathToDB(conn, destFile.getAbsolutePath());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void copyImage(String sourcePath, String destPath) throws IOException {
        Files.copy(Paths.get(sourcePath), Paths.get(destPath),
                StandardCopyOption.REPLACE_EXISTING);
    }

    private Connection connectToDB() {
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

    private void saveImagePathToDB(Connection conn, String imagePath) {
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

    private void showImageDisplayPage(List<File> images) throws FileNotFoundException {
        ImageDisplayPage imageDisplayPage = new ImageDisplayPage(images);
        imageDisplayPage.start(new Stage());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
