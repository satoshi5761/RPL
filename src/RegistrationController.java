
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;

import javax.swing.Action;

import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;

public class RegistrationController {
    @FXML
    private Button mulai_input;

    @FXML
    private TextField name_input;

    @FXML
    private TextField password_input;

    @FXML
    private Hyperlink login;

    @FXML
    public void ClickedMulai(ActionEvent e) {
        /*
         * User melakukan registrasi akun
         */
        String name = name_input.getText();
        String pass = password_input.getText();

        if (name.trim() == "" || pass.trim() == "") {
            // pesan error 
        } else {
            // into database
            
        }
    }

    Stage getStage(ActionEvent e) {
        /* Mendapatkan Stage dari node objek e yang di lakukan action*/ 
        return (Stage) ( ((Node) e.getSource()).getScene().getWindow() );
    }

    @FXML
    public void ClickedLoginDisini(ActionEvent e) throws IOException {
        /* User yang sudah memiliki akun */
        System.out.println("Login clicked");

        FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = fxml_load.load();
        
        Stage currStage = getStage(e);
        currStage.setScene(new Scene(root));
        currStage.show();

    }


}
