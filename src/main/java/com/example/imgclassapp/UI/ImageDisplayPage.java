package com.example.imgclassapp.UI;

import com.example.imgclassapp.controler.ImageManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class ImageDisplayPage extends Application {

    private List<File> images;
    private ImageManager imgM;

    public ImageDisplayPage(List<File> images, ImageManager imgM) {
        this.images = images;
        this.imgM = imgM;
    }

    @Override
    public void start(Stage stage) {

        TilePane tilePane = new TilePane();
        tilePane.setHgap(10);
        tilePane.setVgap(10);
        tilePane.setPrefWidth(700);
        tilePane.setPrefHeight(700);

        tilePane.getStyleClass().add("gallry");

        for (int i = 0; i < images.size(); i++) {
            File imageFile = images.get(i);
            Image image = new Image(imageFile.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
            imageView.setOnMouseClicked(e -> showLargeImage(imageFile));

            Button deleteButton = new Button("x");

            VBox imgC = new VBox(imageView, deleteButton);

            tilePane.getChildren().add(imgC);

            deleteButton.setOnAction(event -> {
                imgM.deleteFileFromProjectFolder(imageFile);
                tilePane.getChildren().remove(imgC);
            });
        }


        ScrollPane root = new ScrollPane(tilePane);
        root.setFitToWidth(true);
        root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.getStyleClass().add("root");

        Scene imageScene = new Scene(root, 800, 600);
        imageScene.getStylesheets().add(getClass().getResource("/styles/classification.css").toExternalForm());

        stage.setTitle("Images");
        stage.setScene(imageScene);
        stage.show();
    }

    private void showLargeImage(File file) {
        Stage stage = new Stage();
        Image image = new Image(file.toURI().toString());

        CustomImageView imageView = new CustomImageView(image, file.getAbsolutePath());
        imageView.setPreserveRatio(true);

        imageView.setFitHeight(600);
        imageView.setFitWidth(800);

        BorderPane pane = new BorderPane();
        pane.setCenter(imageView);

        Scene scene = new Scene(pane, 800, 600);

        stage.setTitle("Image");
        stage.setScene(scene);
        stage.show();
    }
}
