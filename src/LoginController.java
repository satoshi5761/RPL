
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.Node;

public class LoginController {

    @FXML
    private Button login_input;

    @FXML
    private TextField name_input;

    @FXML
    private TextField password_input;

    @FXML
    public void ClickedLogin() throws Exception {
        String username = name_input.getText();
        String passwd = password_input.getText();

        System.out.println(username);
        System.out.println(passwd);

        DatabaseController db = new DatabaseController();
        if ( db.login(username, passwd) == true ) {
            /* Masuk ke akun user */
        } else {
            Alert a = new Alert(AlertType.ERROR, "Try again");
            a.show();
        }
        
        db.tutup_cinta();
    }

}