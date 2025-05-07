package prlbo.project.rpl.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Node;

public class RegistrationController {

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnRegis;

    @FXML
    private TextField txtNama;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtPassword1;

    Stage getStage(ActionEvent e) {
        /* Mendapatkan Stage dari node objek e yang di lakukan action*/
        return (Stage) ( ((Node) e.getSource()).getScene().getWindow() );
    }

    private void Error(String pesan){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("INFORMASI");
        alert.setHeaderText("Error");
        alert.setContentText(pesan);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/Asset/To-Do-List.png")));
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/Asset/Style.css").toExternalForm());
        dialogPane.getStyleClass().add("CustomNotif");
        alert.showAndWait();
    }

    private void Berhasil(String pesan){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("INFORMASI");
        alert.setHeaderText("Berhasil");
        alert.setContentText(pesan);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/Asset/To-Do-List.png")));
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/Asset/Style.css").toExternalForm());
        dialogPane.getStyleClass().add("CustomNotif");
        alert.showAndWait();
    }

    @FXML
    void Login(ActionEvent event) throws IOException {
        /* User yang sudah memiliki akun pindah ke Login.fxml*/
        System.out.println("Login clicked");

        FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/login.fxml"));
        Parent root = fxml_load.load();

        Stage currStage = getStage(event);
        currStage.setScene(new Scene(root));
        currStage.show();
    }

    @FXML
    void Registrasi(ActionEvent event) throws Exception {
        /*
         * User melakukan registrasi akun
         */
        String name = txtNama.getText();
        String pass = txtPassword.getText();
        String pass1 = txtPassword1.getText();

        if (name.trim().isEmpty() || pass.trim().isEmpty() || pass1.trim().isEmpty()) {
            /* Ada field input yang belum diisi */
            Error("Ada Field Yang Kosong");

        } else if (!pass.equals(pass1)) {
            Error("Inputan tidak sesuai (Password Berbeda)");
        } else {
            // Register user ke dalam database;
            DatabaseController db = new DatabaseController();
            boolean success = db.register(name, pass); db.tutup_cinta();
            txtNama.clear();
            txtPassword.clear();
            txtPassword1.clear();
            Berhasil("Berhasil Silahkan Lanjut Login");
            try {
                FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/login.fxml"));
                Parent root = fxml_load.load();

                Stage currStage = getStage(event);
                currStage.setScene(new Scene(root));
                currStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!success) {
                /* pop up GUI */
                Error("Username Sudah Tersedia");
            }
        }
    }

}