package com.example.imgclassapp;


import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class welcome extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Create main container with center alignment
        VBox root = new VBox(30); // 30px spacing between elements
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("welcome-container");

        // Welcome message
        Label welcomeLabel = new Label("Welcome to ADA/Classifier");
        welcomeLabel.getStyleClass().add("welcome-text");

        // Start button
        Button startButton = new Button("Start Classifying");
        startButton.getStyleClass().add("start-button");

        // Add elements to container
        root.getChildren().addAll(welcomeLabel, startButton);

        // Create scene with responsive size
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/welcome.css").toExternalForm());

        // Set button action to switch to classification page
        startButton.setOnAction(e -> {
            classification classificationPage = new classification();
            try {
                classificationPage.start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Configure stage
        primaryStage.setTitle("Classifier Application");
        primaryStage.setMinWidth(400);  // Minimum width
        primaryStage.setMinHeight(300); // Minimum height
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}