package com.example.sortview.controler;

import com.example.sortview.model.DatabaseManager;
import com.example.sortview.ui.CustomImageView;
import com.example.sortview.ui.ImageClassificationUI;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Controler {
    private ImageManager imageManager;
    private List<String> classifications;
    private TilePane classesArea;
    private static final Logger logger = LoggerFactory.getLogger(Controler.class);

    public Controler(ImageManager imageManager, List<String> classifications, TilePane classesArea) {
        this.imageManager = imageManager;
        this.classesArea = classesArea;
        this.classifications = classifications;
    }

    public Controler(ImageManager imageManager, TilePane classesArea) {
        this.imageManager = imageManager;
        this.classesArea = classesArea;
    }

    public void showIMagesInClasses() throws FileNotFoundException {
        classifications = imageManager.getClassesNames();
        for (int j = 0; j < classifications.size(); j++) {
            List<File> files = null;
            files = imageManager.getImagesForClassification(classifications.get(j));
            displayImagesFromDirectory(files, j, classifications, classifications.get(j));
        }
    }

    private void displayImagesFromDirectory(List<File> files, int classificationIndex, List<String> classifications, String s) throws FileNotFoundException {
        if (files != null && !files.isEmpty()) {
            for (int i = 0; i < Math.min(files.size(), 4); i++) {
                File imageFile = files.get(i);
                Image image = new Image(new FileInputStream(imageFile));
                String imagePath = imageFile.getAbsolutePath();
                CustomImageView imageView = new CustomImageView(image, imagePath);
                imageView.setFitWidth(60);
                imageView.setFitHeight(60);
                DatabaseManager dbManager = new DatabaseManager();
                List<String> classesNamesOld = null;
                try (Connection conn = dbManager.connectToDatabase()) {
                    classesNamesOld = dbManager.getClassesNamesOld(conn);
                } catch (SQLException e) {
                      logger.error("An error occurred: ", e);
                }
                if (classesNamesOld.size() < classifications.size() && !classesNamesOld.contains(s)) {
                    createChild(s);
                    try (Connection conn = dbManager.connectToDatabase()) {
                        dbManager.saveCLassName(conn, s);
                    } catch (SQLException e) {
                        logger.error("An error occurred: ", e);
                    }
                }
                VBox vBox = (VBox) this.classesArea.getChildren().get(classificationIndex);
                GridPane classAlbum = (GridPane) vBox.getChildren().get(0);

                classAlbum.add(imageView, i % 2, i / 2);
            }
        }
    }

    private void createChild(String s) {
        RightSectionsControler r = new RightSectionsControler(imageManager, classesArea);

        GridPane classAlbum = new GridPane();
        classAlbum.getStyleClass().add("classAlbum-area");
        classAlbum.setHgap(20);
        classAlbum.setVgap(20);
        classAlbum.setPrefSize(170, 170);


        Label classAlbumName = new Label(s);
        classAlbumName.getStyleClass().add("classAlbumName-label");

        VBox contactClassAlbum = new VBox(5);
        contactClassAlbum.getChildren().addAll(classAlbum, classAlbumName);

        classesArea.getChildren().add(contactClassAlbum);

        contactClassAlbum.setOnMouseClicked(event -> {
            try {
                r.showImageDisplayPage(imageManager.getImagesForClassification(s));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        ImageClassificationUI.setRightSection(classesArea);
    }
}
