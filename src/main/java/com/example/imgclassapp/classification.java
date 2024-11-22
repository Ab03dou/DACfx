package com.example.imgclassapp;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class classification extends Application {

    private static final String IMAGE_DIRECTORY = "images";

    private static final String[] CLASSIFICATIONS = { "All", "Document", "People", "Animals", "Nature", "Screenshot",
            "Other" };

    private TilePane classButtonsArea;

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {

        // Main container using HBox for left-right split
        HBox root = new HBox(20);
        root.getStyleClass().add("main-container");

        // Left section for classifications
        VBox leftSection = new VBox();
        leftSection.getStyleClass().add("left-section");

        // Create GridPane to arrange the squares
        classButtonsArea = new TilePane();
        classButtonsArea.getStyleClass().add("class-buttons-area");

        for (int i = 0; i < CLASSIFICATIONS.length; i++) {
            GridPane contactAr = new GridPane();
            contactAr.getStyleClass().add("content-area");
            contactAr.setHgap(10);
            contactAr.setVgap(10);

            Label contactArName = new Label(CLASSIFICATIONS[i]);
            contactArName.getStyleClass().add("image-name-label");

            VBox contactContainer = new VBox(5); // 5 is the spacing between elements
            contactContainer.getChildren().addAll(contactAr, contactArName);

            classButtonsArea.getChildren().add(contactContainer);

            final int index = i;
            contactContainer.setOnMouseClicked(event -> {
                try {
                    showImageDisplayPage(getFilesForClassification(CLASSIFICATIONS[index]));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });

        }
        showIMages();

        leftSection.getChildren().addAll(classButtonsArea);

        // Right section for upload
        VBox rightSection = new VBox(20);
        rightSection.getStyleClass().add("right-section");

        VBox uploadArea = new VBox(15);
        uploadArea.getStyleClass().add("upload-area");

        Label uploadLabel = new Label("Classify Your Images with AI");
        uploadLabel.getStyleClass().add("upload-label");


        Label subLabel = new Label("Select your images below and let our AI classify them instantly");
        subLabel.getStyleClass().add("sub-label");

        Button addButton = new Button("Choose Files");
        addButton.getStyleClass().add("add-button");

        uploadArea.getChildren().addAll(uploadLabel,subLabel, addButton);
        rightSection.getChildren().addAll(uploadArea);

        // Add sections to root
        root.getChildren().addAll(rightSection, leftSection);

        // File chooser function
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        addButton.setOnAction(event -> {
            List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
            if (files != null) {
                for (File file : files) {
                    try {
                        // TODO: fi next sprint nbdlo fl code hada bah iwli ikhyr class bl ai mch tji 1
                        // kima dok
                        saveFileToProjectFolder(file, CLASSIFICATIONS[1]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Create and set scene
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/styles/classification.css").toExternalForm());

        // Configure stage
        primaryStage.setTitle("Image Classification");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private List<File> getFilesForClassification(String classification) throws FileNotFoundException {
        List<File> files = new ArrayList<>();
        if (!classification.equals("All")) {
            File directory = new File(IMAGE_DIRECTORY + "/" + classification);
            File[] directoryFiles = directory.listFiles(
                    (dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));
            if (directoryFiles != null) {
                files.addAll(Arrays.asList(directoryFiles));
            }
        } else {
            File[] directories = new File(IMAGE_DIRECTORY).listFiles(File::isDirectory);
            if (directories != null) {
                for (File directory : directories) {
                    File[] directoryFiles = directory.listFiles(
                            (dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));
                    if (directoryFiles != null) {
                        files.addAll(Arrays.asList(directoryFiles));
                    }
                }
            }
        }
        return files;
    }

    private void showIMages() throws FileNotFoundException {
        for (int j = 0; j < CLASSIFICATIONS.length; j++) {
            List<File> files = getFilesForClassification(CLASSIFICATIONS[j]);
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
                VBox vBox = (VBox) classButtonsArea.getChildren().get(classificationIndex);
                GridPane contactAr = (GridPane) vBox.getChildren().get(0);
                contactAr.add(imageView, i % 2, i / 2);
            }
        }
    }

    private void saveFileToProjectFolder(File originalFile, String dir) throws IOException {
        File directory = new File(IMAGE_DIRECTORY + "/" + dir);

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
        showIMages();

    }

    private void showImageDisplayPage(List<File> images) throws FileNotFoundException {
        ImageDisplayPage imageDisplayPage = new ImageDisplayPage(images);
        imageDisplayPage.start(new Stage());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
