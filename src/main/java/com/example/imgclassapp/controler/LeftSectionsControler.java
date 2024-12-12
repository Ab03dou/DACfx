package com.example.imgclassapp.controler;

import com.example.imgclassapp.UI.CustomImageView;
import javafx.scene.control.Label;
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
    private ArrayList<String> classifications;
    private TilePane classesArea;

    public LeftSectionsControler(ImageManager imageManager, ArrayList<String> classifications,TilePane classesArea) {
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
            handleFileUpload(files, resVBOX,primaryStage);
        }
    }

    private void handleFileUpload(List<File> files, VBox resVBOX, Stage primaryStage) {
        for (File file : files) {
            try {
                String className = "";
                double confidence = 0;
                try {
                    String scriptPath = "src/main/resources/aiModel/imageClassification.py";
                    String result = PythonScriptExecutor.executePythonScript(scriptPath, file.getAbsolutePath());
                    String[] parts = result.split(" ");
                    className = parts[0];
                    confidence = Double.parseDouble(parts[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle error appropriately in your UI
                }
                imageManager.saveFileToProjectFolder(file, className, confidence);
                showIMagesInResClasses(resVBOX, file,className);
                showIMagesInClasses();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showIMagesInResClasses(VBox h, File imageFile, String className) throws FileNotFoundException {

        classifications = imageManager.getClassesNames();
        Image image = new Image(new FileInputStream(imageFile));
        String imagePath = imageFile.getAbsolutePath();
        CustomImageView imageView = new CustomImageView(image, imagePath);
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        HBox b = new HBox();
        Label l = new Label("Classified as: " + className);
        b.getChildren().addAll(imageView, l);
        h.getChildren().add(b);
        b.getStyleClass().add("res-class-area");
    }
}
