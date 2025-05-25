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

import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.util.Duration;
import prlbo.project.rpl.Manager.UserManager;
import prlbo.project.rpl.data.User;
import prlbo.project.rpl.util.PesanMessage;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

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

    @FXML
    void Login(ActionEvent event) throws Exception {
        String username = txtNama.getText();
        String passwd = txtPassword.getText();
        DatabaseController db = new DatabaseController();
        if (db.login(username, passwd) == true) {
            TrayNotification tray = new TrayNotification();
            tray.setTitle("Welcome To Our ToDoListApp");
            tray.setMessage("Hello " + username);
            tray.setNotificationType(NotificationType.SUCCESS);
            tray.showAndDismiss(Duration.seconds(3));

            /* Masuk ke akun user */
            txtNama.clear();
            txtPassword.clear();

            FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/main.fxml"));

            String query = "SELECT id_account,username FROM account WHERE username = '" + username + "'";
            Connection conn = DriverManager.getConnection("jdbc:sqlite:DMAC.db");
            PreparedStatement stmt = conn.prepareStatement(query);

            ResultSet resultSet = stmt.executeQuery();
            System.out.println("Hello");
            while (resultSet.next()) {
                int id = resultSet.getInt("id_account");
                User user = new User(id, username, passwd);
                System.out.println(user.getUsername());
                UserManager.setCurrentUser(user);
            }
            Parent root = fxml_load.load();
            MainController main = fxml_load.getController();
            Stage currStage = getStage(event);
            currStage.setScene(new Scene(root));
            currStage.show();
        } else {
            PesanMessage.tampilpesan(AlertType.INFORMATION, "INFORMASI", "Error", "Login Gagal (Password atau Username Salah)");
            txtNama.clear();
            txtPassword.clear();
        }
        db.tutup_cinta();
    }

    @FXML
    void Lupa(ActionEvent event) throws IOException {
        FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/LupaPassword.fxml"));
        Parent root = fxml_load.load();
        Stage currStage = getStage(event);
        currStage.setScene(new Scene(root));
        currStage.show();
    }

    Stage getStage(ActionEvent e) {
        /* Mendapatkan Stage dari node objek e yang di lakukan action*/
        return (Stage) (((Node) e.getSource()).getScene().getWindow());
    }

    @FXML
    void Signup(ActionEvent event) throws IOException {
        FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/Registration.fxml"));
        Parent root = fxml_load.load();
        Stage currStage = getStage(event);
        currStage.setScene(new Scene(root));
        currStage.show();
    }
}