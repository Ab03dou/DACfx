package com.example.sortview.controler;

import javafx.application.Platform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import com.example.sortview.ui.ErrorHandler;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ErrorHandlingControler {
    public boolean checkImages(List<File> files) {
        for (int i = 0; i < files.size(); i++) {
            long MAX_FILE_SIZE = 5L * 1024 * 1024; // 5 MB
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


    public boolean canContnuie(double v) throws InterruptedException {
        return createErrorMsgGUI("the image could not be classified with confidence.\nthe model may not support this type of images. do you want to continue?", v);
    }

    public void createErrorMsgGUI(String errorMsg) {
        ErrorHandler errorHandler = new ErrorHandler(errorMsg);
        errorHandler.start(new Stage());
    }

    private boolean createErrorMsgGUI(String errorMsg, double v) throws InterruptedException {
        ErrorHandler errorHandler = new ErrorHandler(errorMsg,v);
        return errorHandler.askForCon();
    }

    public boolean isValidImage(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            if (image != null) {
                return true;
            }
        } catch (IOException e) {
            System.out.println("Error reading the image: " + e.getMessage());
        }
        return false;
    }
}
