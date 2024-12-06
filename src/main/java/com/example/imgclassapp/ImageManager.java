package com.example.imgclassapp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Manages Image-related Operations
public class ImageManager {
    private static final String IMAGE_DIRECTORY = "images";
    private DatabaseManager dbManager;

    public ImageManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void saveFileToProjectFolder(File originalFile, String classification) throws IOException {
        File directory = new File(IMAGE_DIRECTORY + "/" + classification);

        if (!directory.exists()) {
            Files.createDirectories(directory.toPath());
        }

        File destFile = new File(directory, originalFile.getName());
        copyImage(originalFile.getAbsolutePath(), destFile.getAbsolutePath());

        try (Connection conn = dbManager.connectToDatabase(IMAGE_DIRECTORY)) {
            dbManager.saveImagePath(conn, destFile.getAbsolutePath());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void copyImage(String sourcePath, String destPath) throws IOException {
        Files.copy(Paths.get(sourcePath), Paths.get(destPath),
                StandardCopyOption.REPLACE_EXISTING);
    }

    public List<File> getImagesForClassification(String classification) {
        try (Connection conn = dbManager.connectToDatabase(IMAGE_DIRECTORY)) {
            return dbManager.getFilesForClassification(classification, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}

