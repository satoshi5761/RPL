
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
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
    public void ClickedLogin() {
        String username = name_input.getText();
        String passwd = password_input.getText();

        System.out.println(username);
        System.out.println(passwd);
    }

}