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

    opens com.example.imgclassapp to javafx.fxml;
    exports com.example.imgclassapp;
}