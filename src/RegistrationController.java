import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class RegistrationController {

    @FXML
    private Button mulai_input;

    @FXML
    private TextField name_input;

    @FXML
    private TextField password_input;

    @FXML
    void ClickedMulai() {
        String name = name_input.getText();
        String pass = password_input.getText();

        System.out.println(name);
        System.out.println(pass);
    }

}
