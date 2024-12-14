package com.example.sortview.controler;

import com.example.sortview.model.DatabaseManager;

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

    public void saveFileToProjectFolder(File originalFile, String[] classification) throws IOException {
        File directory = new File(IMAGE_DIRECTORY + "/" + classification[0]);

        if (!directory.exists()) {
            Files.createDirectories(directory.toPath());
        }

        File destFile = new File(directory, originalFile.getName());
        if (destFile.exists()) {
            return;
        }
        copyImage(originalFile.getAbsolutePath(), destFile.getAbsolutePath());

        try (Connection conn = dbManager.connectToDatabase(IMAGE_DIRECTORY)) {
            dbManager.saveImagePath(conn, destFile.getAbsolutePath(),classification[0],Double.parseDouble(classification[1]));
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

    public ArrayList<String> getClassesNames() {
        ArrayList<String> list = new ArrayList<>();
        list.add("All");
        try (Connection conn = dbManager.connectToDatabase(IMAGE_DIRECTORY)) {
            list.addAll(dbManager.getClassesNamesDB(conn));
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }
}

