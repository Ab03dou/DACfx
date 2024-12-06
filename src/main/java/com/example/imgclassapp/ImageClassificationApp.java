package com.example.imgclassapp;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.FileNotFoundException;

public class ImageClassificationApp extends Application {
    private static final String[] CLASSIFICATIONS = {"All", "Document", "People", "Animals", "Nature", "Screenshot", "Other"};
    private ImageClassificationUI ui;
    private DatabaseManager dbManager;
    private ImageManager imageManager;

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {

        dbManager = new DatabaseManager();
        imageManager = new ImageManager(dbManager);
        ui = new ImageClassificationUI(primaryStage, CLASSIFICATIONS, imageManager);

        ui.setupUI();
    }

    public static void main(String[] args) {
        launch(args);
    }
}