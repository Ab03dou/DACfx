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
        VBox root = new VBox();
        Image errorImageView;
        HBox hBox = null;
        if (v != -1) {
            errorImageView = new Image(getClass().getResource("/icons/warning.png").toExternalForm());
            Button continueBtn = new Button("Continue");
            continueBtn.setOnAction(event -> {
                        this.canContinue = true;
                        stage.close();
                    }
            );
            Button closeBtn = new Button("Close");
            closeBtn.setOnAction(event -> {
                this.canContinue = false;
                stage.close();
            });
            hBox = new HBox(10,continueBtn, closeBtn);
            hBox.setAlignment(Pos.CENTER_LEFT); // Align items to the left
            hBox.setPadding(new Insets(10)); // Padding around the HBox

            // Add margins for each button
            HBox.setMargin(continueBtn, new Insets(5)); // Margins for the continue button
            HBox.setMargin(closeBtn, new Insets(5)); // Margins for the close button

        } else {
            errorImageView = new Image(getClass().getResource("/icons/error-icon.png").toExternalForm());
        }
        ImageView imageView = new ImageView(errorImageView);
        imageView.setFitWidth(35);
        imageView.setFitHeight(35);
        Label errorLabel = new Label(errorMsg);
        errorLabel.setWrapText(true);
        if (hBox != null) {
            root.getChildren().addAll(hBox);
        }
        root.getChildren().add(0, imageView);
        root.getChildren().add(1, errorLabel);
        root.getStyleClass().add("root");
        errorLabel.getStyleClass().add("label");
        root.getStylesheets().add(getClass().getResource("/styles/Error.css").toExternalForm());

        Scene Error = new Scene(root, 500, 300);
        stage.getIcons().add(errorImageView);
        stage.setTitle("Error");
        stage.setScene(Error);
        stage.setOnHidden(event -> latch.countDown());
        stage.show();
    }

    public boolean askForCon() {
        // Start the JavaFX UI on the JavaFX Application Thread
        Platform.runLater(() -> start(new Stage()));

        try {
            // Wait for the stage to close
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return this.canContinue;
    }
}
