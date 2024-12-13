package com.example.sortview;

import com.example.sortview.UI.ImageClassificationUI;
import com.example.sortview.controler.ImageManager;
import com.example.sortview.model.DatabaseManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class ImageClassificationApp extends Application {
    private ImageClassificationUI ui;
    private DatabaseManager dbManager;
    private ImageManager imageManager;

    @Override
    public void start(Stage primaryStage) throws IOException {
        dbManager = new DatabaseManager();
        imageManager = new ImageManager(dbManager);
        ui = new ImageClassificationUI(primaryStage,imageManager);

        ui.setupUI();
    }

    public static void main(String[] args) {
        launch(args);
    }
}