module org.example.musicplayer {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires jaudiotagger;
    requires javafx.media;

    opens org.example.musicplayer to javafx.fxml;
    exports org.example.musicplayer;
    exports org.example.musicplayer.model;
    exports org.example.musicplayer.service;
    opens org.example.musicplayer.model to javafx.fxml;
    opens org.example.musicplayer.service to javafx.fxml;
}