package com.example.imgclassapp;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class classification extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Main container using HBox for left-right split
        HBox root = new HBox(20);
        root.getStyleClass().add("main-container");

        // Left section for classifications (will now appear on the right)
        VBox leftSection = new VBox(15);
        leftSection.getStyleClass().add("left-section");

        Label classificationLabel = new Label("Image Classifications");
        classificationLabel.getStyleClass().add("section-title");

        VBox classButtonsArea = new VBox(10);
        classButtonsArea.getStyleClass().add("class-buttons-area");

        for (int i = 1; i <= 4; i++) {
            Button classButton = new Button("Class " + i);
            classButton.getStyleClass().add("class-button");

            VBox contentArea = new VBox();
            contentArea.getStyleClass().add("content-area");
            contentArea.setVisible(false);

            classButton.setOnAction(e -> contentArea.setVisible(!contentArea.isVisible()));

            classButtonsArea.getChildren().addAll(classButton, contentArea);
        }

        leftSection.getChildren().addAll(classificationLabel, classButtonsArea);

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
}
