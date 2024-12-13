package com.example.sortview.UI;

import com.example.sortview.controler.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

public class ImageClassificationUI {
    private Stage primaryStage;
    private ArrayList<String[]> classifications;
    private ImageManager imageManager;

    private TilePane classesArea;

    private static VBox rightSection;
    private static VBox resVBOX;

    public ImageClassificationUI(Stage primaryStage, ImageManager imageManager) {
        this.primaryStage = primaryStage;
        this.imageManager = imageManager;
    }

    public static void setRightSection() {
    }

    public void setupUI() throws FileNotFoundException {
        HBox root = createRootLayout();
        VBox leftSection = createLeftSection();
        rightSection = createRightSection();

        root.getChildren().addAll(leftSection, rightSection);

        Scene scene = new Scene(root, 1380, 700);
        scene.getStylesheets().add(getClass().getResource("/styles/classification.css").toExternalForm());

        Image image = new Image(getClass().getResource("/logo/logo.png").toExternalForm());
        primaryStage.getIcons().add(image);
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

        resVBOX = new VBox();
        resVBOX.getStyleClass().add("res-VBOX");
        ScrollPane s = new ScrollPane(resVBOX);

        s.getStyleClass().add("res-SCROLL");

        s.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        s.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        addButton.setOnAction(event -> {
            LeftSectionsControler l = new LeftSectionsControler(imageManager, classifications, classesArea);
            l.loadImage(primaryStage, resVBOX);
        });

        uploadArea.getChildren().addAll(uploadLabel, subLabel, addButton);
        leftSection.getChildren().addAll(uploadArea, s);

        return leftSection;
    }

    private VBox createRightSection() throws FileNotFoundException {
        // Create right section with classification areas
        // Similar to original implementation
        VBox rightSection = new VBox();
        rightSection.getStyleClass().add("right-section");

        classesArea = new TilePane(); // blue kbira
        classesArea.getStyleClass().add("classes-area");

        classifications = imageManager.getClassesNames();
        RightSectionsControler r = new RightSectionsControler(imageManager, classesArea);

        if (classifications.size() != 0) {
            for (int i = 0; i < classifications.size(); i++) {
                GridPane classCn = new GridPane(); // skyBlue sghira
                classCn.getStyleClass().add("classCn-area");
                classCn.setHgap(10);
                classCn.setVgap(10);

                Label classCnName = new Label(classifications.get(i)[0]);
                classCnName.getStyleClass().add("image-name-label");

                VBox contactClassCn = new VBox(5); // 5 is the spacing between elements
                contactClassCn.getChildren().addAll(classCn, classCnName);

                classesArea.getChildren().add(contactClassCn);

                final int index = i;
                contactClassCn.setOnMouseClicked(event -> {
                    try {
                        r.showImageDisplayPage(imageManager.getImagesForClassification(classifications.get(index)[0]));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            r.showIMagesInClasses();
        }

        rightSection.getChildren().addAll(classesArea);
        return rightSection;
    }

    public static void setRightSection(TilePane tp) {
        ImageClassificationUI.rightSection.getChildren().clear();
        ImageClassificationUI.rightSection.getChildren().add(tp);
    }
}
