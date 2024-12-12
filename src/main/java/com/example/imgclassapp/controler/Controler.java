package com.example.imgclassapp.controler;

import com.example.imgclassapp.UI.CustomImageView;
import com.example.imgclassapp.UI.ImageClassificationUI;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Controler {
    private ImageManager imageManager;
    private ArrayList<String> classifications;
    private TilePane classesArea;

    public Controler(ImageManager imageManager, ArrayList<String> classifications, TilePane classesArea) {
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
            displayImagesFromDirectory(files, j, classifications.get(j));
        }
    }

    private void displayImagesFromDirectory(List<File> files, int classificationIndex, String s) throws FileNotFoundException {
        if (files != null && files.size() > 0) {
            for (int i = 0; i < Math.min(files.size(), 4); i++) {
                File imageFile = files.get(i);
                Image image = new Image(new FileInputStream(imageFile));
                String imagePath = imageFile.getAbsolutePath();
                CustomImageView imageView = new CustomImageView(image, imagePath);
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
                if (this.classesArea.getChildren().size() == classificationIndex)
                    createChild(s);
                VBox vBox = (VBox) this.classesArea.getChildren().get(classificationIndex);
                GridPane classCn = (GridPane) vBox.getChildren().get(0);
                classCn.add(imageView, i % 2, i / 2);
            }
        }
    }

    private void createChild(String s) {
        RightSectionsControler r = new RightSectionsControler(imageManager, classesArea);

        GridPane classCn = new GridPane(); // skyBlue sghira
        classCn.getStyleClass().add("classCn-area");
        classCn.setHgap(10);
        classCn.setVgap(10);

        Label classCnName = new Label(s);
        classCnName.getStyleClass().add("image-name-label");

        VBox contactClassCn = new VBox(5); // 5 is the spacing between elements
        contactClassCn.getChildren().addAll(classCn, classCnName);

        classesArea.getChildren().add(contactClassCn);

        contactClassCn.setOnMouseClicked(event -> {
            try {
                r.showImageDisplayPage(imageManager.getImagesForClassification(s));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        ImageClassificationUI.setRightSection(classesArea);

    }
}
