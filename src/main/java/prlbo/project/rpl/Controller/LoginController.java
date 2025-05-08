package prlbo.project.rpl.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.scene.image.Image;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnLupa;

    @FXML
    private Button btnSignUp;

    @FXML
    private TextField txtNama;

    @FXML
    private PasswordField txtPassword;


    private void Error(String pesan){
        Alert alert = new Alert(AlertType.INFORMATION);
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

    @FXML
    void Login(ActionEvent event) throws Exception {
        String username = txtNama.getText();
        String passwd = txtPassword.getText();
        DatabaseController db = new DatabaseController();
        if ( db.login(username, passwd) == true ) {
            /* Masuk ke akun user */
            System.out.println("Login Successful");
            txtNama.clear();
            txtPassword.clear();

            FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/TambahTugas.fxml"));
            Parent root = fxml_load.load();

//            Coba tambah tugas
            TambahTugasController loadtugas = fxml_load.getController();
            String query = "SELECT id FROM account WHERE username = '" + username + "'";
            Connection conn = DriverManager.getConnection("jdbc:sqlite:DMAC.db");
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                loadtugas.set_idacc(id);
            }


            Stage currStage = getStage(event);
            currStage.setScene(new Scene(root));
            currStage.show();
        } else {
            Error("Password / Username Salah");
            txtNama.clear();
            txtPassword.clear();
        }
        db.tutup_cinta();
    }

    @FXML
    void Lupa(ActionEvent event) throws IOException{
        System.out.println("Registration clicked");

        FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/LupaPassword.fxml"));
        Parent root = fxml_load.load();

        Stage currStage = getStage(event);
        currStage.setScene(new Scene(root));
        currStage.show();
    }

    Stage getStage(ActionEvent e) {
        /* Mendapatkan Stage dari node objek e yang di lakukan action*/
        return (Stage) ( ((Node) e.getSource()).getScene().getWindow() );
    }

    @FXML
    void Signup(ActionEvent event) throws IOException {
        System.out.println("Registration clicked");
        FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/Registration.fxml"));
        Parent root = fxml_load.load();
        Stage currStage = getStage(event);
        currStage.setScene(new Scene(root));
        currStage.show();
    }
}