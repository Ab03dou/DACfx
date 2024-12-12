module com.example.imgclassapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires kotlin.stdlib;
    requires java.sql;
    requires javafx.graphics;
    requires java.desktop;

    opens com.example.imgclassapp to javafx.fxml;
    exports com.example.imgclassapp;
    exports com.example.imgclassapp.controler;
    opens com.example.imgclassapp.controler to javafx.fxml;
    exports com.example.imgclassapp.UI;
    opens com.example.imgclassapp.UI to javafx.fxml;
    exports com.example.imgclassapp.model;
    opens com.example.imgclassapp.model to javafx.fxml;
}