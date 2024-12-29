package com.example.sortview.UI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;


public class ErrorHandler extends Application {

    private double v = -1;
    private String errorMsg;
    private boolean canContinue;
    private final CountDownLatch latch = new CountDownLatch(1);

    public ErrorHandler(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public ErrorHandler(String errorMsg, double v) {
        this.errorMsg = errorMsg;
        this.v = v;
    }


    @Override
    public void start(Stage stage) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));

        Image errorImageView;
        if (v != -1) {
            errorImageView = new Image(getClass().getResource("/icons/warning.png").toExternalForm());
            errorMsg += "\nThe confidence for this image is " + v + "%";
        } else {
            errorImageView = new Image(getClass().getResource("/icons/error-icon.png").toExternalForm());
        }

        ImageView imageView = new ImageView(errorImageView);
        imageView.setFitWidth(35);
        imageView.setFitHeight(35);
        imageView.getStyleClass().add("img");

        Label errorLabel = new Label(errorMsg);
        errorLabel.setWrapText(true);
        errorLabel.getStyleClass().add("label");

        HBox logoImg = new HBox(20, imageView, errorLabel);
        logoImg.setAlignment(Pos.CENTER_LEFT);

        int errorScenceHigh=110;
        HBox hBox = null;
        if (v != -1) {
            errorScenceHigh=140;
            Button continueBtn = new Button("Continue");
            continueBtn.setOnAction(event -> {
                this.canContinue = true;
                stage.close();
            });

            Button closeBtn = new Button("Close");
            closeBtn.setOnAction(event -> {
                this.canContinue = false;
                stage.close();
            });

            hBox = new HBox(10, continueBtn, closeBtn);
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.setPadding(new Insets(10));
        }else{
            Button closeBtn = new Button("Close");
            closeBtn.setOnAction(event -> {
                this.canContinue = false;
                stage.close();
            });

            hBox = new HBox(closeBtn);
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.setPadding(new Insets(10));
        }
        root.getChildren().add(logoImg);
        if (hBox != null) {
            root.getChildren().add(hBox);
        }

        root.getStyleClass().add("root");
        root.getStylesheets().add(getClass().getResource("/styles/Error.css").toExternalForm());

        Scene errorScene = new Scene(root, 500, errorScenceHigh);
        stage.getIcons().add(errorImageView);
        stage.setTitle("Error");
        stage.setScene(errorScene);
        stage.setOnHidden(event -> latch.countDown());
        stage.show();
    }


    public boolean askForCon() {
        Platform.runLater(() -> start(new Stage()));

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return this.canContinue;
    }
}
