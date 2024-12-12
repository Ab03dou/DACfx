package com.example.imgclassapp.controler;

import com.example.imgclassapp.UI.ImageDisplayPage;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class RightSectionsControler extends Controler {


    private ImageManager imageManager;

    public RightSectionsControler(ImageManager imageManager, TilePane classesArea) {
        super(imageManager, classesArea);
        this.imageManager = imageManager;
    }

    public void showImageDisplayPage(List<File> images) throws FileNotFoundException {
        ImageDisplayPage imageDisplayPage = new ImageDisplayPage(images, imageManager);
        imageDisplayPage.start(new Stage());
    }
}
