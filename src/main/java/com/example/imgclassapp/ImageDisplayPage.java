package com.example.imgclassapp;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class ImageDisplayPage {

    private Stage stage;
    private List<File> images;

    public ImageDisplayPage(Stage stage, List<File> images) {
        this.stage = stage;
        this.images = images;
    }

    public void show() {

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Add each image to the grid
        for (int i = 0; i < images.size(); i++) {
            File imageFile = images.get(i);
            Image image = new Image(imageFile.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(true);

            // Add the image to the grid
            gridPane.add(imageView, i % 4, i / 4);  // 4 images per row
        }

        // Wrap the grid in a StackPane with a light blue background
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: lightblue;");
        root.getChildren().add(gridPane);

        // Create and set the scene for the new window
        Scene imageScene = new Scene(root, 800, 600);
        stage.setTitle("Images in Square");
        stage.setScene(imageScene);
        stage.show();
    }
}
