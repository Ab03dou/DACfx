package com.example.imgclassapp;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CustomImageView extends ImageView {
    private final String filePath;

    public CustomImageView(Image image, String filePath) {
        super(image);
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}