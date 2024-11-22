package com.example.imgclassapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class ImageDisplayPage extends Application {

    private List<File> images;

    public ImageDisplayPage(List<File> images) {
        this.images = images;
    }

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {

        TilePane tilePane = new TilePane();
        tilePane.setHgap(10);
        tilePane.setVgap(10);
        tilePane.setPrefWidth(700);
        tilePane.setPrefHeight(700);

        tilePane.getStyleClass().add("gallry");

        // Add each image to the grid
        for (int i = 0; i < images.size(); i++) {
            File imageFile = images.get(i);
            Image image = new Image(imageFile.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
            imageView.setOnMouseClicked(e -> showLargeImage(imageFile));

            tilePane.getChildren().add(imageView);
        }
        Button backButton = new Button("<-");
        backButton.setOnAction(e -> {
            primaryStage.close();
        });
        ScrollPane scrollPane = new ScrollPane(tilePane);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        // Wrap the grid in a StackPane with a light blue background
        VBox root = new VBox(backButton, scrollPane);
        root.getStyleClass().add("root");

        // Create and set the scene for the new window
        Scene imageScene = new Scene(root, 800, 600);
        imageScene.getStylesheets().add(getClass().getResource("/styles/classification.css").toExternalForm());

        primaryStage.setTitle("Images in Square");
        primaryStage.setScene(imageScene);
        primaryStage.show();
    }

    private void showLargeImage(File file) {
        Stage stage = new Stage();
        Image image = new Image(file.toURI().toString());

        CustomImageView imageView = new CustomImageView(image, file.getAbsolutePath());
        imageView.setPreserveRatio(true);

        // Fit image to the stage dimensions
        imageView.setFitHeight(600);
        imageView.setFitWidth(800);

        BorderPane pane = new BorderPane();
        pane.setCenter(imageView);

        Scene scene = new Scene(pane, 800, 600);

        stage.setTitle("Enlarged Image");
        stage.setScene(scene);
        stage.show();
    }
}
