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
import prlbo.project.rpl.util.PesanMessage;

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
        String name = txtNama.getText().trim();
        String pass = txtPassword.getText();
        String pass1 = txtPassword1.getText();

        if (name.isEmpty() || pass.isEmpty() || pass1.isEmpty()) {
            PesanMessage.tampilpesan(AlertType.INFORMATION, "INFORMASI", "Error", "Ada Field Yang Kosong");
        } else if (!pass.equals(pass1)) {
            PesanMessage.tampilpesan(AlertType.INFORMATION, "INFORMASI", "Error", "Password tidak cocok");
        } else if (pass.length() < 8) {
            PesanMessage.tampilpesan(AlertType.INFORMATION, "INFORMASI", "Error", "Password minimal 8 karakter");
        } else {
            // Register user ke dalam database
            DatabaseController db = new DatabaseController();
            boolean success = db.register(name, pass);
            db.tutup_cinta();

            if (success) {
                txtNama.clear();
                txtPassword.clear();
                txtPassword1.clear();
                PesanMessage.tampilpesan(AlertType.INFORMATION, "INFORMASI", "Berhasil", "Registrasi berhasil! Silakan login.");

                try {
                    FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/login.fxml"));
                    Parent root = fxml_load.load();
                    Stage currStage = getStage(event);
                    currStage.setScene(new Scene(root));
                    currStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                PesanMessage.tampilpesan(AlertType.INFORMATION, "INFORMASI", "Error", "Username sudah tersedia");
            }
        }
    }


}