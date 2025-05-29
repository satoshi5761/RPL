package prlbo.project.rpl.util;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class PesanMessage {
    public static Alert tampilpesan(AlertType type, String title, String header, String pesan) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(pesan);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(PesanMessage.class.getResourceAsStream("/Asset/To-Do-List.png")));

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(PesanMessage.class.getResource("/Asset/Style.css").toExternalForm());
        dialogPane.getStyleClass().add("CustomNotif");
        alert.showAndWait();
        return alert;
    }

    public static boolean selesai_atau_tidak(String title, String header, String pesan) {
        /* prompt untuk tugas yang sudah selesai atau tidak terselesaikan */
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(pesan);

        ButtonType selesai = new ButtonType("Selesai", ButtonBar.ButtonData.YES);
        ButtonType tidak_selesai = new ButtonType("Tidak Selesai", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(selesai, tidak_selesai);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(PesanMessage.class.getResourceAsStream("/Asset/To-Do-List.png")));
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(PesanMessage.class.getResource("/Asset/Style.css").toExternalForm());
        dialogPane.getStyleClass().add("CustomNotif");

        Optional<ButtonType> res = alert.showAndWait();
        System.out.println(res.get().getButtonData());
        return res.get().getButtonData() == ButtonBar.ButtonData.YES;

    }
}