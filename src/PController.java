import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class PController {
    
    @FXML
    private TextField input_panjang;

    @FXML
    private TextField input_lebar;

    @FXML
    private Label output;

    @FXML
    private Button btn_calc;

    public void calculate() {
        Integer p = Integer.parseInt(input_panjang.getText());
        Integer l = Integer.parseInt(input_lebar.getText());

        output.setText(String.valueOf(p*l));

    }
}
