package prlbo.project.rpl.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import prlbo.project.rpl.util.PesanMessage;

import java.io.IOException;

public class LupaPasswordController {

    @FXML
    private Button btnUbah;

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
    void Kembali(ActionEvent event) throws IOException {
        FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/login.fxml"));
        Parent root = fxml_load.load();
        Stage currStage = getStage(event);
        currStage.setScene(new Scene(root));
        currStage.show();
    }

    @FXML
    void Ubah(ActionEvent event) throws Exception{
        String nama = txtNama.getText();
        String password = txtPassword.getText();
        String password1 = txtPassword1.getText();
        if(nama.trim().isEmpty() || password.trim().isEmpty() || password1.trim().isEmpty()) {
            PesanMessage.tampilpesan(Alert.AlertType.ERROR, "INFORMASI", "Error", "Inputan tidak sesuai");
        } else if(password.equals(password1)) {
            DatabaseController db = new DatabaseController();
            int sucess = db.forgot(nama, password); db.tutup_cinta();
            if(sucess == 1) {
                System.out.println("Ubah Clicked");
                FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/login.fxml"));
                Parent root = fxml_load.load();
                Stage currStage = getStage(event);
                currStage.setScene(new Scene(root));
                currStage.show();
            } else if (sucess == 0){
                PesanMessage.tampilpesan(Alert.AlertType.ERROR, "INFORMASI", "Error", "Gagal Ubah Password");
            } else {
                PesanMessage.tampilpesan(Alert.AlertType.ERROR, "INFORMASI", "Error", "Username tidak ditemukan");
            }
        } else {
            PesanMessage.tampilpesan(Alert.AlertType.ERROR, "INFORMASI", "Error", "Gagal Ubah Password\n(Password pertama tidak sesuai\ndengan password kedua)");
        }
    }

}
