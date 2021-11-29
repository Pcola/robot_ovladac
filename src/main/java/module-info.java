module sk.spu.robot_ovladac {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.fazecast.jSerialComm;

    opens sk.spu.robot_ovladac to javafx.fxml;
    exports sk.spu.robot_ovladac;
}