package prlbo.project.rpl.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import prlbo.project.rpl.util.PesanMessage;

import java.io.IOException;
import java.time.LocalDate;

public class TambahTugasController {
    @FXML
    private Button btntambah;

    @FXML
    private ComboBox<String> combxkategori;

    @FXML
    private ComboBox<String> combxwaktu;

    @FXML
    private DatePicker dateduedate;

    @FXML
    private TextField txtnama;

    private int idacc;

    public void set_idacc(int id) {
        idacc = id;
    }

    public void initialize() {
        loadcomboboxkategori();
    }

    Stage getStage(ActionEvent e) {
        /* Mendapatkan Stage dari node objek e yang di lakukan action*/
        return (Stage) (((Node) e.getSource()).getScene().getWindow());
    }

    @FXML
    void addTugas(ActionEvent event) throws IOException {
        String nama = txtnama.getText();
        LocalDate tanggal = dateduedate.getValue();
        String kategori = combxkategori.getValue();
        if (nama.isEmpty() || tanggal == null) {
            PesanMessage.tampilpesan(Alert.AlertType.ERROR, "INFORMASI", "Error", "Pastikan semua data terisi!");
        } else {
            try {
                DatabaseController db = new DatabaseController();
                System.out.println(idacc);
                if (db.TambahTugas(idacc, nama, tanggal, kategori)) {
                    FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/main.fxml"));
                    Parent root = fxml_load.load();
                    MainController main = fxml_load.getController();
                    Stage currStage = getStage(event);
                    currStage.setScene(new Scene(root));
                    currStage.show();
                    db.tutup_cinta();
                }
            } catch (Exception e) {
                e.printStackTrace();
                PesanMessage.tampilpesan(Alert.AlertType.ERROR, "INFORMASI", "Error", "Terjadi Kesalahan!");
            }
        }
    }

    private void loadcomboboxkategori() {
        ObservableList<String> kategori = FXCollections.observableArrayList();
        try {
            DatabaseController db = new DatabaseController();
            kategori = db.loadcomboboxkat();

            if (kategori.size() > 0) {
                combxkategori.setItems(kategori);
            } else {

            }
        } catch (Exception e) {
            System.out.println("blog");
        }
    }
}
