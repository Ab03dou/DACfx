package com.example.sortview.controler;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.example.sortview.UI.ErrorHandler;
import javafx.application.Platform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ErrorHandlingControler {
    public boolean checkImages(List<File> files) {
        // Check if the file is valid
        for (int i = 0; i < files.size(); i++) {
            long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
            if (files.get(i).length() <= MAX_FILE_SIZE) {
                if (!isValidImage(files.get(i))) {
                    int finalI = i;
                    Platform.runLater(() -> {
                        createErrorMsgGUI("corrupted image file. Please check your image: " + files.get(finalI).getAbsolutePath());
                    });
                    return false;
                }
            } else {
                int finalI = i;
                Platform.runLater(() -> {
                    createErrorMsgGUI("Image too large. Please select an image less than 5MB: " + files.get(finalI).getAbsolutePath());
                });
                return false;
            }
        }
        return true;
    }


    public void createErrorMsgGUI(String empty) {
        ErrorHandler errorHandler = new ErrorHandler(empty);
        errorHandler.start(new Stage());
    }

    public boolean isValidImage(File file) {
        try {
            // Attempt to read the file as an image
            BufferedImage image = ImageIO.read(file);
            if (image != null) {
                return true; // File is a valid image
            }
        } catch (IOException e) {
            System.out.println("Error reading the image: " + e.getMessage());
        }
        return false; // File is not a valid image
    }
}
