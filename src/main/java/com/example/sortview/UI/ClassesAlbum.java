package com.example.sortview.UI;

import com.example.sortview.controler.ImageManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class ClassesAlbum extends Application {

    private List<File> images;
    private ImageManager imgM;

    public ClassesAlbum(List<File> images, ImageManager imgM) {
        this.images = images;
        this.imgM = imgM;
    }

    @Override
    public void start(Stage stage) {

        TilePane album = new TilePane();
        album.setHgap(10);
        album.setVgap(10);
        album.setPrefWidth(700);
        album.setPrefHeight(700);

        album.getStyleClass().add("album");

        for (int i = 0; i < images.size(); i++) {
            File imageFile = images.get(i);
            Image image = new Image(imageFile.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
            imageView.setOnMouseClicked(e -> showLargeImage(imageFile));

            VBox imgC = new VBox(imageView);

            album.getChildren().add(imgC);
        }


        ScrollPane root = new ScrollPane(album);
        root.setFitToWidth(true);
        root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.getStyleClass().add("root-SCROLL");

        Scene imageScene = new Scene(root, 800, 600);
        imageScene.getStylesheets().add(getClass().getResource("/styles/album.css").toExternalForm());

        Image image = new Image(getClass().getResource("/logo/logo-icon.png").toExternalForm());
        stage.getIcons().add(image);
        stage.setTitle("Images");
        stage.setMaximized(true);
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
        Image imageLogo = new Image(getClass().getResource("/logo/logo-icon.png").toExternalForm());
        stage.getIcons().add(imageLogo);
        stage.setTitle("Image");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }
}
