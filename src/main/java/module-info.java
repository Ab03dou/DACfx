module com.example.sortview {
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
    requires metadata.extractor;
    requires org.slf4j;

    opens com.example.sortview to javafx.fxml;
    exports com.example.sortview;
    exports com.example.sortview.controler;
    opens com.example.sortview.controler to javafx.fxml;
    exports com.example.sortview.ui;
    opens com.example.sortview.ui to javafx.fxml;
    exports com.example.sortview.model;
    opens com.example.sortview.model to javafx.fxml;
}