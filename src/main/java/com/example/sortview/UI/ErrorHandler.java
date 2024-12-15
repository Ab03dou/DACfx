package com.example.sortview.UI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class ErrorHandler extends Application {

    private String errorMsg;

    public ErrorHandler(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();

        Image errorImageView = new Image(getClass().getResource("/icons/error-icon.png").toExternalForm());
        ImageView imageView = new ImageView(errorImageView);
        imageView.setFitWidth(35);
        imageView.setFitHeight(35);
        Label errorLabel = new Label(errorMsg);
        errorLabel.setWrapText(true);
        root.getChildren().addAll(imageView, errorLabel);
        root.getStyleClass().add("root");
        errorLabel.getStyleClass().add("label");
        root.getStylesheets().add(getClass().getResource("/styles/Error.css").toExternalForm());

        Scene Error = new Scene(root, 400, 150);
        stage.getIcons().add(errorImageView);
        stage.setTitle("Error");
        stage.setScene(Error);
        stage.show();
    }
}
