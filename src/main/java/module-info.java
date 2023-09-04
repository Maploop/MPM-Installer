module me.maploop.mpmsetup {
      requires javafx.controls;
      requires javafx.fxml;
      requires javafx.web;

      requires org.controlsfx.controls;
      requires com.dlsc.formsfx;
      requires net.synedra.validatorfx;
      requires org.kordamp.ikonli.javafx;
      requires org.kordamp.bootstrapfx.core;
      requires com.almasb.fxgl.all;
      requires org.json;

      opens me.maploop.mpmsetup to javafx.fxml;
      exports me.maploop.mpmsetup;
}