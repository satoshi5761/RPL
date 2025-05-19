
package prlbo.project.rpl.Controller;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;


public class UncompletedTaskController {

    private int idacc;

    public void setIdacc(int id) {
        idacc = id;
    }

    @FXML
    private TableColumn<TugasSelesai, String> No;

    @FXML
    private TableColumn<TugasSelesai, String> Namatugas;

    @FXML
    private TableColumn<TugasSelesai, String> Kategori;

    @FXML
    private TableColumn<TugasSelesai, String> Tenggat;

    @FXML
    private TableView<TugasSelesai> CompletedTable;

    @FXML
    private Button btnKembali;

    @FXML
    public void initialize() throws Exception {
        No.setCellValueFactory(new PropertyValueFactory<>("no"));
        Namatugas.setCellValueFactory(new PropertyValueFactory<>("namaTugas"));
        Kategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        Tenggat.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        DatabaseController db = new DatabaseController();
        CompletedTable.setItems(db.ShowUncompletedTaskDB(idacc));

        db.tutup_cinta();
    }

    Stage getStage(ActionEvent e) {
        /* Mendapatkan Stage dari node objek e yang di lakukan action*/
        return (Stage) (((Node) e.getSource()).getScene().getWindow());
    }

    @FXML
    void Kembali(ActionEvent event) throws IOException {
        FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/main.fxml"));
        Parent root = fxml_load.load();
        Stage currStage = getStage(event);
        currStage.setScene(new Scene(root));
        currStage.show();
    }

}