package com.example.imgclassapp;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

class ImageClassificationUI {
    private Stage primaryStage;
    private String[] classifications;
    private ImageManager imageManager;

    private TilePane classesArea;

    public ImageClassificationUI(Stage primaryStage, String[] classifications, ImageManager imageManager) {
        this.primaryStage = primaryStage;
        this.classifications = classifications;
        this.imageManager = imageManager;
    }

    public void setupUI() throws FileNotFoundException {
        HBox root = createRootLayout();
        VBox leftSection = createLeftSection();
        VBox rightSection = createRightSection();

        root.getChildren().addAll(leftSection, rightSection);

        Scene scene = new Scene(root, 1380, 700);
        scene.getStylesheets().add(getClass().getResource("/styles/classification.css").toExternalForm());

        primaryStage.setTitle("Image Classification");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createRootLayout() {
        HBox root = new HBox(100);
        root.getStyleClass().add("main-container");
        return root;
    }

    private VBox createLeftSection() {
        // Create left section with upload area and file chooser
        // Similar to original implementation

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

        VBox resVBOX = new VBox();
        resVBOX.getStyleClass().add("res-VBOX");
        ScrollPane s = new ScrollPane(resVBOX);
        s.getStyleClass().add("res-SCROLL");

        s.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        s.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        addButton.setOnAction(event -> {
            List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
            if (files != null) {
                handleFileUpload(files, resVBOX);
            }
        });


        uploadArea.getChildren().addAll(uploadLabel, subLabel, addButton);
        leftSection.getChildren().addAll(uploadArea, s);

        return leftSection;
    }

    private void handleFileUpload(List<File> files, VBox resVBOX) {
        for (File file : files) {
            try {
                imageManager.saveFileToProjectFolder(file, classifications[1]);
                showIMagesInResClasses(resVBOX, file, 1);
                showIMagesInClasses();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private VBox createRightSection() throws FileNotFoundException {
        // Create right section with classification areas
        // Similar to original implementation
        VBox rightSection = new VBox();
        rightSection.getStyleClass().add("right-section");

        classesArea = new TilePane(); // blue kbira
        classesArea.getStyleClass().add("classes-area");

        for (int i = 0; i < classifications.length; i++) {
            GridPane classCn = new GridPane(); // skyBlue sghira
            classCn.getStyleClass().add("classCn-area");
            classCn.setHgap(10);
            classCn.setVgap(10);

            Label classCnName = new Label(classifications[i]);
            classCnName.getStyleClass().add("image-name-label");

            VBox contactClassCn = new VBox(5); // 5 is the spacing between elements
            contactClassCn.getChildren().addAll(classCn, classCnName);

            classesArea.getChildren().add(contactClassCn);

            final int index = i;
            contactClassCn.setOnMouseClicked(event -> {
                try {
                    showImageDisplayPage(imageManager.getImagesForClassification(classifications[index]));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        showIMagesInClasses();

        rightSection.getChildren().addAll(classesArea);
        return rightSection;
    }

    public void showIMagesInClasses() throws FileNotFoundException {
        for (int j = 0; j < classifications.length; j++) {
            List<File> files = null;
            files = imageManager.getImagesForClassification(classifications[j]);
            displayImagesFromDirectory(files, j);
        }
    }

    private void showIMagesInResClasses(VBox h, File imageFile, int r) throws FileNotFoundException {

        Image image = new Image(new FileInputStream(imageFile));
        String imagePath = imageFile.getAbsolutePath();
        CustomImageView imageView = new CustomImageView(image, imagePath);
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        HBox b = new HBox();
        Label l = new Label("Classified as: " + classifications[r]);
        b.getChildren().addAll(imageView, l);
        h.getChildren().add(b);
        b.getStyleClass().add("res-class-area");
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

    private void showImageDisplayPage(List<File> images) throws FileNotFoundException {
        ImageDisplayPage imageDisplayPage = new ImageDisplayPage(images,imageManager);
        imageDisplayPage.start(new Stage());
    }
}
