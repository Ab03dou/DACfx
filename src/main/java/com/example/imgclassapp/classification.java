package com.example.imgclassapp;

import javafx.application.Application;
import javafx.geometry.Pos;
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
import java.util.ArrayList;
import java.util.List;

public class classification extends Application {

    private static final String IMAGE_DIRECTORY = "images";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "0100mysql0445";

    private GridPane classButtonsArea; // GridPane for squares
    private List<File> uploadedFiles = new ArrayList<>(); // Store uploaded images
    private List<List<File>> imageLists = new ArrayList<>(); // List of lists to store images for each square
    private List<VBox> contentAreas = new ArrayList<>(); // To track content areas (squares)

    @Override
    public void start(Stage primaryStage) {

        // Main container using HBox for left-right split
        HBox root = new HBox(20);
        root.getStyleClass().add("main-container");

        // Left section for classifications
        VBox leftSection = new VBox(15);
        leftSection.getStyleClass().add("left-section");

        // Create GridPane to arrange the squares
        classButtonsArea = new GridPane();
        classButtonsArea.getStyleClass().add("class-buttons-area");

        // Adjust the layout properties for the grid
        classButtonsArea.setHgap(20); // Horizontal gap between squares
        classButtonsArea.setVgap(20); // Vertical gap between squares

        // Create 6 squares in total (4 in the first row, 2 in the second)
        int rows = 2; // 2 rows
        int cols = 4; // 4 squares in the first row, 2 in the second

        // Loop to create the squares
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < (row == 0 ? 4 : 2); col++) { // First row has 4 squares, second row has 2
                VBox contentArea = new VBox(5);
                contentArea.getStyleClass().add("content-area");
                contentArea.setPrefWidth(200);  // Set width of square
                contentArea.setPrefHeight(200); // Set height of square
                contentArea.setAlignment(Pos.CENTER);

                // Store the content area in the list for tracking
                contentAreas.add(contentArea);
                imageLists.add(new ArrayList<>()); // Initialize an empty list for each square

                // Add the contentArea (square) to the GridPane
                classButtonsArea.add(contentArea, col, row);

                // Add a click event to the square (VBox)
                final int index = row * cols + col;  // Calculate the index of the clicked square
                contentArea.setOnMouseClicked(event -> {
                    // Open the ImageDisplayPage with the images from the clicked square
                    showImageDisplayPage(primaryStage, imageLists.get(index));
                });
            }
        }

        leftSection.getChildren().addAll(classButtonsArea);

        // Right section for upload
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

        // Add sections to root
        root.getChildren().addAll(rightSection, leftSection);

        // File chooser function
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        addButton.setOnAction(event -> {
            List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);

            if (files != null) {
                uploadedFiles.clear(); // Clear previously selected files
                uploadedFiles.addAll(files); // Add selected files to the list

                try {
                    // Load the images into the squares
                    for (int i = 0; i < uploadedFiles.size(); i++) {
                        File selectedFile = uploadedFiles.get(i); // Get each selected file
                        Image image = new Image(new FileInputStream(selectedFile));

                        // Ensure the image fits within the square but is smaller than 200px
                        ImageView imageView = new ImageView(image);
                        imageView.setFitWidth(150);  // Set the width of the image to 150px
                        imageView.setFitHeight(150); // Set the height of the image to 150px
                        imageView.setPreserveRatio(true); // Maintain aspect ratio

                        // Create a Label for the image name
                        Label imageNameLabel = new Label(selectedFile.getName());
                        imageNameLabel.getStyleClass().add("image-name-label");

                        // Get the content area (square) and display the image and name
                        if (i < contentAreas.size()) {
                            VBox square = contentAreas.get(i);
                            square.getChildren().clear(); // Clear any previous content
                            square.getChildren().addAll(imageView, imageNameLabel); // Add the image and name

                            // Add this image to the corresponding square's list
                            imageLists.get(i).add(selectedFile);
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
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

    // Method to show the ImageDisplayPage with a list of images
    private void showImageDisplayPage(Stage primaryStage, List<File> images) {
        ImageDisplayPage imageDisplayPage = new ImageDisplayPage(primaryStage, images);
        imageDisplayPage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
