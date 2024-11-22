package com.example.imgclassapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class classification extends Application {

    private static final String IMAGE_DIRECTORY = "images";

    private static final String[] CLASSIFICATIONS = { "All", "Document", "People", "Animals", "Nature", "Screenshot",
            "Other" };

    private TilePane classesArea;

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {

        HBox root = new HBox(100);
        root.getStyleClass().add("main-container");

        VBox rightSection = new VBox();
        rightSection.getStyleClass().add("right-section");

        classesArea = new TilePane();
        classesArea.getStyleClass().add("classes-area");

        for (int i = 0; i < CLASSIFICATIONS.length; i++) {
            GridPane classCn = new GridPane();
            classCn.getStyleClass().add("classCn-area");
            classCn.setHgap(10);
            classCn.setVgap(10);

            Label classCnName = new Label(CLASSIFICATIONS[i]);
            classCnName.getStyleClass().add("image-name-label");

            VBox contactClassCn = new VBox(5); // 5 is the spacing between elements
            contactClassCn.getChildren().addAll(classCn, classCnName);

            classesArea.getChildren().add(contactClassCn);

            final int index = i;
            contactClassCn.setOnMouseClicked(event -> {
                try {
                    showImageDisplayPage(getFilesForClassification(CLASSIFICATIONS[index]));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }

        showIMagesInClasses();

        rightSection.getChildren().addAll(classesArea);

        VBox leftSection = new VBox(20);
        leftSection.getStyleClass().add("left-section");

        VBox uploadArea = new VBox(15);
        uploadArea.getStyleClass().add("upload-area");

        Label uploadLabel = new Label("Classify Your Images with AI");
        uploadLabel.getStyleClass().add("upload-label");

        Label subLabel = new Label("Select your images below and let our AI classify them instantly");
        subLabel.getStyleClass().add("sub-label");

        Button addButton = new Button("Choose Files");
        addButton.getStyleClass().add("add-button");

        uploadArea.getChildren().addAll(uploadLabel,subLabel, addButton);
        leftSection.getChildren().addAll(uploadArea);

        root.getChildren().addAll(leftSection, rightSection);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        addButton.setOnAction(event -> {
            List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
            if (files != null) {
                for (File file : files) {
                    try {
                        // TODO: fi next sprint nbdlo fl code hada bah iwli ikhyr class bl ai mch tji
                        // kima dok
                        saveFileToProjectFolder(file, CLASSIFICATIONS[1]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Scene scene = new Scene(root, 1380, 700);
        scene.getStylesheets().add(getClass().getResource("/styles/classification.css").toExternalForm());

        primaryStage.setTitle("Image Classification");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private List<File> getFilesForClassification(String classification) throws FileNotFoundException {
        List<File> files = new ArrayList<>();
        if (!classification.equals("All")) {
            File directory = new File(IMAGE_DIRECTORY + "/" + classification);
            File[] directoryFiles = directory.listFiles(
                    (dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpeg"));
            if (directoryFiles != null) {
                files.addAll(Arrays.asList(directoryFiles));
            }
        } else {
            File[] directories = new File(IMAGE_DIRECTORY).listFiles(File::isDirectory);
            if (directories != null) {
                for (File directory : directories) {
                    File[] directoryFiles = directory.listFiles(
                            (dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpeg"));
                    if (directoryFiles != null) {
                        files.addAll(Arrays.asList(directoryFiles));
                    }
                }
            }
        }
        return files;
    }

    private void showIMagesInClasses() throws FileNotFoundException {
        for (int j = 0; j < CLASSIFICATIONS.length; j++) {
            List<File> files = getFilesForClassification(CLASSIFICATIONS[j]);
            displayImagesFromDirectory(files, j);
        }
    }

    private void displayImagesFromDirectory(List<File> files, int classificationIndex) throws FileNotFoundException {

        if (files != null && files.size() > 0) {
            for (int i = 0; i < Math.min(files.size(), 4); i++) {
                File imageFile = files.get(i);
                Image image = new Image(new FileInputStream(imageFile));
                String imagePath = imageFile.getAbsolutePath();
                CustomImageView imageView = new CustomImageView(image, imagePath);
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
                VBox vBox = (VBox) classesArea.getChildren().get(classificationIndex);
                GridPane classCn = (GridPane) vBox.getChildren().get(0);
                classCn.add(imageView, i % 2, i / 2);
            }
        }
    }

    private void saveFileToProjectFolder(File originalFile, String dir) throws IOException {
        File directory = new File(IMAGE_DIRECTORY + "/" + dir);

        if (!directory.exists()) {
            Files.createDirectories(directory.toPath());
        }

        File destFile = new File(directory, originalFile.getName());
        try (FileInputStream inputStream = new FileInputStream(originalFile);
                FileOutputStream outputStream = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
        showIMagesInClasses();

    }

    private void showImageDisplayPage(List<File> images) throws FileNotFoundException {
        ImageDisplayPage imageDisplayPage = new ImageDisplayPage(images);
        imageDisplayPage.start(new Stage());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
