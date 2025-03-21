package com.example.sortview.controler;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.sortview.ui.CustomImageView;

class HandleFileUpload extends Controler  implements Runnable {

    private ImageManager imageManager;
    private File file;
    private VBox resVBOX;
    private ImageView imageView;
    private static final Logger logger = LoggerFactory.getLogger(HandleFileUpload.class);


    public HandleFileUpload(ImageManager imageManager, List<String> classifications, TilePane classesArea,File file, VBox resVBOX, ImageView imageView) {
        super(imageManager, classifications, classesArea);
        this.imageManager = imageManager;
        this.file = file;
        this.imageView = imageView;
        this.resVBOX = resVBOX;
    }

    @Override
    public void run() {
        try {
            ProgressBar bar = new ProgressBar();
            bar.setPrefWidth(500);
            bar.setPrefHeight(20);
            Platform.runLater(() -> {
                resVBOX.getChildren().remove(imageView);
                resVBOX.getChildren().addFirst(bar);
            });

            String[] classRes = {};
            boolean canContnuie = true;
            try {
                String scriptPath = "src/main/resources/aiModel/imageClassification.py";
                String result = PythonScriptExecutor.executePythonScript(scriptPath, file.getAbsolutePath());
                classRes = result.split(" ");
                if (Double.parseDouble(classRes[1]) < 50) { // classRes[1] is the confidence of the image
                    ErrorHandlingControler errorHandlingControler = new ErrorHandlingControler();
                    canContnuie = errorHandlingControler.canContnuie(Double.parseDouble(classRes[1]));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Preserve interrupted status
                throw new RuntimeException("Thread was interrupted", e); // Optionally rethrow
            } catch (Exception e) {
                  logger.error("An error occurred: ", e); // Handle other exceptions
            }
            if (canContnuie) {
                imageManager.saveFileToProjectFolder(file, classRes);

                String[] finalClassRes = classRes;
                Platform.runLater(() -> {
                    try {
                        showIMagesInResClasses(resVBOX, bar, file, finalClassRes);
                    } catch (FileNotFoundException e) {
                          logger.error("An error occurred: ", e);
                    }
                });

                Platform.runLater(() -> {
                    try {
                        showIMagesInClasses();
                    } catch (FileNotFoundException e) {
                          logger.error("An error occurred: ", e);
                    }
                });
            } else {
                Platform.runLater(() -> {
                    resVBOX.getChildren().removeFirst();
                });
            }
        } catch (IOException e) {
              logger.error("An error occurred: ", e);
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

public class LeftSectionsControler extends Controler {

    private ImageManager imageManager;
    private List<String> classifications;
    private TilePane classesArea;

    public LeftSectionsControler(ImageManager imageManager, List<String> classifications2, TilePane classesArea) {
        super(imageManager, classifications2, classesArea);
        this.classifications=classifications2;
        this.imageManager = imageManager;
        this.classesArea=classesArea;
    }

    public void loadImage(Stage primaryStage, VBox resVBOX, ImageView imageView) throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));


        List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
        if (files != null) {

            ExecutorService executor = Executors.newFixedThreadPool(files.size());
            ErrorHandlingControler errorHandlingControler = new ErrorHandlingControler();
            if (errorHandlingControler.checkImages(files)) {
                for (File file : files) {
                    executor.submit(new HandleFileUpload(imageManager, classifications, classesArea,file, resVBOX, imageView));
                }
            }
            executor.shutdown();
        } else {
            ErrorHandlingControler errorHandlingControler = new ErrorHandlingControler();
            errorHandlingControler.createErrorMsgGUI("you didn't select any images for classify pleas try again.");
        }
    }

}
