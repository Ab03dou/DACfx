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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageManager {
    private static final String IMAGE_DIRECTORY = "images";
    private DatabaseManager dbManager;
    private static final Logger logger = LoggerFactory.getLogger(ImageManager.class);


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

        try (Connection conn = dbManager.connectToDatabase()) {
            dbManager.saveImagePath(conn, destFile.getAbsolutePath(), classification[0], Double.parseDouble(classification[1]));
        } catch (SQLException e) {
              logger.error("An error occurred: ", e);
        }
    }

    public void copyImage(String sourcePath, String destPath) throws IOException {
        Files.copy(Paths.get(sourcePath), Paths.get(destPath),
                StandardCopyOption.REPLACE_EXISTING);
    }

    public List<File> getImagesForClassification(String classification) {
        try (Connection conn = dbManager.connectToDatabase()) {
            return dbManager.getFilesForClassification(classification, conn);
        } catch (SQLException e) {
              logger.error("An error occurred: ", e);
            return new ArrayList<>();
        }
    }

    public List<String> getClassesNames() {
        List<String> list = new ArrayList<>();

        try (Connection conn = dbManager.connectToDatabase()) {
            list.addAll(dbManager.getClassesNamesNew(conn));
            return list;
        } catch (SQLException e) {
              logger.error("An error occurred: ", e);
            return new ArrayList<>();
        }
    }
}

