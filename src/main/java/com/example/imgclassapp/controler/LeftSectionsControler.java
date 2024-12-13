package com.example.imgclassapp.controler;

import com.example.imgclassapp.UI.CustomImageView;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LeftSectionsControler extends Controler{

    private ImageManager imageManager;
    private ArrayList<String[]> classifications;
    private TilePane classesArea;

    public LeftSectionsControler(ImageManager imageManager, ArrayList<String[]> classifications,TilePane classesArea) {
        super(imageManager,classifications,classesArea);
        this.imageManager = imageManager;
        this.classifications = classifications;
        this.classesArea=classesArea;
    }

    public void loadImage(Stage primaryStage, VBox resVBOX) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
        if (files != null) {
            Task<Void> uploadTask = new Task<>() {
                @Override
                protected Void call() {
                    handleFileUpload(files, resVBOX);
                    return null;
                }
            };

            new Thread(uploadTask).start();
        }
    }

    private void handleFileUpload(List<File> files, VBox resVBOX) {
        for (File file : files) {
            try {

                ProgressBar bar = new ProgressBar();
                Platform.runLater(() -> {
                    bar.setProgress(-1.0f);
                    resVBOX.getChildren().add(0,bar);
                });

                String[] classRes = {};
                double confidence = 0;
                try {
                    String scriptPath = "src/main/resources/aiModel/imageClassification.py";
                    String result = PythonScriptExecutor.executePythonScript(scriptPath, file.getAbsolutePath());
                    classRes = result.split(" ");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                imageManager.saveFileToProjectFolder(file, classRes, confidence);

                // Use Platform.runLater to update the UI
                String[] finalClassRes = classRes;
                Platform.runLater(() -> {
                    try {
                        showIMagesInResClasses(resVBOX, bar, file, finalClassRes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                });

                Platform.runLater(() -> {
                    try {
                        showIMagesInClasses();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showIMagesInResClasses(VBox h, ProgressBar bar, File imageFile, String[] finalClassRes) throws FileNotFoundException {
        Image image = new Image(new FileInputStream(imageFile));
        String imagePath = imageFile.getAbsolutePath();
        CustomImageView imageView = new CustomImageView(image, imagePath);
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        HBox b = new HBox();
        Label name = new Label("Classified as: " + finalClassRes[0]);
        Label prec = new Label(finalClassRes[1]+"%");
        b.getChildren().addAll(imageView, name,prec);
        b.getStyleClass().add("res-class-area");

        // UI updates must be done on the JavaFX Application Thread
        h.getChildren().remove(bar);
        h.getChildren().add(0,b);
    }

}
