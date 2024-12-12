package com.example.imgclassapp.controler;

import com.example.imgclassapp.UI.CustomImageView;
import com.example.imgclassapp.UI.ImageDisplayPage;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Controler {
    private ImageManager imageManager;
    private ArrayList<String> classifications;
    private TilePane classesArea;

    public Controler(ImageManager imageManager,ArrayList<String> classifications, TilePane classesArea) {
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
                VBox vBox = (VBox) this.classesArea.getChildren().get(classificationIndex);
                GridPane classCn = (GridPane) vBox.getChildren().get(0);
                classCn.add(imageView, i % 2, i / 2);
            }
        }
    }
}
