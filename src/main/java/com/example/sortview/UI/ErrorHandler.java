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
        imageView.setFitWidth(25);
        imageView.setFitHeight(25);
        Label errorLabel = new Label(errorMsg);
        root.getChildren().addAll(imageView, errorLabel);
        root.getStyleClass().add("-fx-padding: 30px;");


        Scene Error = new Scene(root, 200, 100);
        stage.setTitle("Error");
        stage.setScene(Error);
        stage.show();
    }
}
