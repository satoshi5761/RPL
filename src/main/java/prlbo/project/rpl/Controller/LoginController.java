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
    public void Login(ActionEvent event) throws Exception {
        String username = txtNama.getText().trim();
        String passwd = txtPassword.getText().trim();

        if (username.isEmpty() || passwd.isEmpty()) {
            PesanMessage.tampilpesan(AlertType.ERROR, "Login Gagal", "Data Kosong", "Username atau password tidak boleh kosong!");
            return;
        }

        DatabaseController db = new DatabaseController();

        try {
            if (db.login(username, passwd)) {
                // Login berhasil
                // Ambil data user dengan PreparedStatement
                String query = "SELECT id_account, username FROM account WHERE username = ?";
                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:DMAC.db");
                     PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, username);
                    ResultSet resultSet = stmt.executeQuery();
                    if (resultSet.next()) {
                        int id = resultSet.getInt("id_account");
                        User user = new User(id, username);
                        UserManager.setCurrentUser(user);
                    }
                }

                // Load Main.fxml
                FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/main.fxml"));
                Parent root = fxml_load.load();
                Stage stage = getStage(event);
                stage.setScene(new Scene(root));
                stage.show();

                // Notifikasi sukses
                TrayNotification tray = new TrayNotification();
                tray.setTitle("Welcome To Our ToDoListApp");
                tray.setMessage("Hello " + username);
                tray.setNotificationType(NotificationType.SUCCESS);
                tray.showAndDismiss(Duration.seconds(1));

                // Clear field
                txtNama.clear();
                txtPassword.clear();

            } else {
                PesanMessage.tampilpesan(AlertType.INFORMATION, "Login Gagal", "Error", "Username atau Password salah.");
                txtNama.clear();
                txtPassword.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            PesanMessage.tampilpesan(AlertType.ERROR, "Error", "Terjadi Kesalahan", "Gagal login karena kesalahan sistem.");
        } finally {
            db.tutup_database();
        }
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