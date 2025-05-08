module prlbo.project.rpl {
    requires javafx.fxml;
    requires java.sql;
    requires javafx.controls;
    requires java.desktop;
    requires jdk.compiler;


    opens prlbo.project.rpl to javafx.fxml;
    exports prlbo.project.rpl;
    exports prlbo.project.rpl.Controller;
    opens prlbo.project.rpl.Controller to javafx.fxml;
}