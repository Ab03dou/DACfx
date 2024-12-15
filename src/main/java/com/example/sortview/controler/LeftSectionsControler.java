package com.example.sortview.controler;

import com.example.sortview.UI.CustomImageView;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LeftSectionsControler extends Controler {

    private ImageManager imageManager;

    public LeftSectionsControler(ImageManager imageManager, ArrayList<String> classifications, TilePane classesArea) {
        super(imageManager, classifications, classesArea);
        this.imageManager = imageManager;
    }

    public void loadImage(Stage primaryStage, VBox resVBOX, ImageView imageView) throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));


        List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
        if (files != null) {
            Task<Void> uploadTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    ErrorHandlingControler errorHandlingControler = new ErrorHandlingControler();
                    if (errorHandlingControler.checkImages(files))
                        handleFileUpload(files, resVBOX, imageView);
                    return null;
                }
            };
            new Thread(uploadTask).start();
        } else {
            ErrorHandlingControler errorHandlingControler = new ErrorHandlingControler();
            errorHandlingControler.createErrorMsgGUI("you didn't select any images for classify pleas try again.");
        }
    }

    private void handleFileUpload(List<File> files, VBox resVBOX, ImageView imageView) {
        for (File file : files) {
            try {
                ProgressBar bar = new ProgressBar();
                bar.setPrefWidth(500);
                bar.setPrefHeight(20);
                Platform.runLater(() -> {
                    resVBOX.getChildren().remove(imageView);
                    resVBOX.getChildren().add(0, bar);
                });

                String[] classRes = {};
                boolean canContnuie = true;
                try {
                    String scriptPath = "src/main/resources/aiModel/imageClassification.py";
                    String result = PythonScriptExecutor.executePythonScript(scriptPath, file.getAbsolutePath());
                    classRes = result.split(" ");
                    if (Double.parseDouble(classRes[1]) < 50) {
                        ErrorHandlingControler errorHandlingControler = new ErrorHandlingControler();
                        canContnuie = errorHandlingControler.canContnuie(Double.parseDouble(classRes[1]));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (canContnuie) {
                    imageManager.saveFileToProjectFolder(file, classRes);

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
                } else {
                    resVBOX.getChildren().remove(bar);
                }
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
        Label prec = new Label(finalClassRes[1] + "%");
        b.getChildren().addAll(imageView, name, prec);
        b.getStyleClass().add("res-class-area");

        h.getChildren().remove(bar);
        h.getChildren().add(0, b);
    }

}
