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
                } else {
                    File correctedFile = handleImageOrientation(files.get(i));
                    if (correctedFile != null) {
//                        files.remove(i);
//                        files.add(correctedFile);
                    } else {
                        int finalI = i;
                        Platform.runLater(() -> {
                            createErrorMsgGUI("Orientation Metadata Missing. Please check your image: " + files.get(finalI).getAbsolutePath());
                        });
                        return false;
                    }
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

    public File handleImageOrientation(File imageFile) {
        try {
            // Read the image file
            BufferedImage image = ImageIO.read(imageFile);
            if (image == null) {
                throw new IOException("Invalid image file.");
            }

            // Extract EXIF metadata
            Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
            ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            BufferedImage correctedImage = image; // Default: original image
            if (directory != null && directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
                int orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);

                // Rotate or flip the image based on orientation
                switch (orientation) {
                    case 1: // Normal
                        break;
                    case 3: // Upside down
                        correctedImage = rotateImage(image, 180);
                        break;
                    case 6: // Rotated 90 degrees clockwise
                        correctedImage = rotateImage(image, 90);
                        break;
                    case 8: // Rotated 90 degrees counterclockwise
                        correctedImage = rotateImage(image, 270);
                        break;
                    default:
                        System.out.println("Unknown orientation: " + orientation);
                        break;
                }
            } else {
                System.out.println("No orientation metadata found. Returning original image.");
            }

            // Write corrected image to a temporary file
            File tempFile = File.createTempFile("corrected_image", ".jpg");
            ImageIO.write(correctedImage, "jpg", tempFile);

            System.out.println("Corrected image saved temporarily at: " + tempFile.getAbsolutePath());
            return tempFile;

        } catch (Exception e) {
            System.err.println("Error processing image orientation: " + e.getMessage());
            return null;
        }
    }

    private BufferedImage rotateImage(BufferedImage img, int angle) {
        int width = img.getWidth();
        int height = img.getHeight();

        BufferedImage rotatedImage = new BufferedImage(height, width, img.getType());
        Graphics2D graphics = rotatedImage.createGraphics();

        // Rotate the image
        graphics.rotate(Math.toRadians(angle), height / 2.0, height / 2.0);
        graphics.translate((height - width) / 2, (width - height) / 2);
        graphics.drawImage(img, 0, 0, null);
        graphics.dispose();

        return rotatedImage;
    }
}
