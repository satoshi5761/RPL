package prlbo.project.rpl.Controller;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class CompletedTaskController {

    @FXML
    private TableColumn<ObservableList<String>, String> Namatugas;

    @FXML
    private TableColumn<ObservableList<String>, String> No;

    @FXML
    private TableColumn<ObservableList<String>, String> Selesai;

    @FXML
    private TableColumn<ObservableList<String>, String> Tenggat;

    @FXML
    private TableView<ObservableList<String>> CompletedTable;


}
