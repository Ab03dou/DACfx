package com.example.sortview.ui;

import com.example.sortview.controler.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

public class ImageClassificationUI {
    private Stage primaryStage;
    private ArrayList<String> classifications;
    private ImageManager imageManager;

    private TilePane classesArea;

    private static VBox rightSection;
    private static VBox resVBOX;

    public ImageClassificationUI(Stage primaryStage, ImageManager imageManager) {
        this.primaryStage = primaryStage;
        this.imageManager = imageManager;
    }

    public void setupUI() throws FileNotFoundException {
        HBox root = createRootLayout();
        VBox leftSection = createLeftSection();
        rightSection = createRightSection();

        root.getChildren().addAll(leftSection, rightSection);

        Scene scene = new Scene(root, 1380, 700);
        scene.getStylesheets().add(getClass().getResource("/styles/classification.css").toExternalForm());

        Image image = new Image(getClass().getResource("/logo/logo-icon.png").toExternalForm());
        primaryStage.getIcons().add(image);
        primaryStage.setTitle("SortView");
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createRootLayout() {
        HBox root = new HBox(100);
        root.getStyleClass().add("root-container");
        return root;
    }

    private VBox createLeftSection() {

        VBox leftSection = new VBox();
        leftSection.getStylesheets().add(getClass().getResource("/styles/LeftSection.css").toExternalForm());
        leftSection.getStyleClass().add("left-section");

        VBox uploadArea = new VBox();
        uploadArea.getStyleClass().add("upload-area");

        Label uploadLabel = new Label("Classify Your Images with SortView");
        uploadLabel.getStyleClass().add("upload-label");

        Label subLabel = new Label("Select your images and let our AI classify them instantly\n\n");
        subLabel.getStyleClass().add("upload-subtext");
        Button uploadButton = new Button("Choose Files");
        uploadButton.getStyleClass().add("upload-button");

        resVBOX = new VBox();
        resVBOX.getStyleClass().add("res-VBOX");
        Image image = new Image(getClass().getResource("/logo/logo.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(300);
        imageView.setFitHeight(300);
        VBox.setMargin(imageView, new Insets(0, 0, 0, 90));
        resVBOX.getChildren().add(0, imageView);

        ScrollPane s = new ScrollPane(resVBOX);
        s.setFitToWidth(true);

        s.getStyleClass().add("res-SCROLL");

        s.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        s.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        uploadButton.setOnAction(event -> {
            LeftSectionsControler l = new LeftSectionsControler(imageManager, classifications, classesArea);
            try {
                l.loadImage(primaryStage, resVBOX, imageView);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        uploadArea.getChildren().addAll(uploadLabel, subLabel, uploadButton);
        leftSection.getChildren().addAll(uploadArea, s);

        return leftSection;
    }

    private VBox createRightSection() throws FileNotFoundException {
        VBox rightSection = new VBox();
        rightSection.getStyleClass().add("right-section");
        rightSection.getStylesheets().add(getClass().getResource("/styles/RightSection.css").toExternalForm());

        classesArea = new TilePane();
        classesArea.setPrefSize(700,700);
        classesArea.getStyleClass().add("classes-area");

        classifications = imageManager.getClassesNames();
        RightSectionsControler r = new RightSectionsControler(imageManager, classesArea);

        if (classifications.size() != 0) {
            for (int i = 0; i < classifications.size(); i++) {
                GridPane classAlbum = new GridPane();
                classAlbum.getStyleClass().add("classAlbum-area");
                classAlbum.setHgap(20);
                classAlbum.setVgap(20);
                classAlbum.setPrefSize(170, 170);


                Label classAlbumName = new Label(classifications.get(i));
                classAlbumName.getStyleClass().add("classAlbumName-label");

                VBox contactClassAlbum = new VBox(5);
                contactClassAlbum.getChildren().addAll(classAlbum, classAlbumName);

                classesArea.getChildren().add(contactClassAlbum);

                final int index = i;
                contactClassAlbum.setOnMouseClicked(event -> {
                    try {
                        r.showImageDisplayPage(imageManager.getImagesForClassification(classifications.get(index)));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            r.showIMagesInClasses();
        }


        ScrollPane s = new ScrollPane(classesArea);

        s.setFitToWidth(true);
        s.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        s.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        s.getStyleClass().add("scroll-pane-section");

        s.setPrefSize(700,700);
        rightSection.getChildren().add(s);
        return rightSection;
    }

    public static void setRightSection(TilePane tp) {
        ImageClassificationUI.rightSection.getChildren().clear();
        ScrollPane s = new ScrollPane(tp);
        s.setFitToWidth(true);
        s.getStyleClass().add("scroll-pane-section");
        s.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        s.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        s.setPrefSize(700,700) ;
        ImageClassificationUI.rightSection.getChildren().removeAll();
        ImageClassificationUI.rightSection.getChildren().add(s);
    }
}
