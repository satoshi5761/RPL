module prlbo.project.rpl {
    requires javafx.base;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.controls;
    requires java.desktop;
    requires jdk.compiler;
    requires java.management;
    requires TrayNotification;


    opens prlbo.project.rpl to javafx.fxml;
    exports prlbo.project.rpl;
    exports prlbo.project.rpl.Controller;
    opens prlbo.project.rpl.Controller to javafx.fxml;
    exports prlbo.project.rpl.Manager;
    opens prlbo.project.rpl.Manager to javafx.fxml;
}