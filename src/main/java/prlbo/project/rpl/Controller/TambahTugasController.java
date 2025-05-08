package prlbo.project.rpl.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import prlbo.project.rpl.util.PesanMessage;

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

    public void initialize(){
        loadcomboboxkategori();
    }

    @FXML
    void addTugas(ActionEvent event) {
        String nama = txtnama.getText();
        LocalDate tanggal = dateduedate.getValue();
        String waktu = combxwaktu.getValue();
        String kategori =  combxkategori.getValue();
        if (nama.isEmpty() || tanggal == null) {
            PesanMessage.tampilpesan(Alert.AlertType.ERROR, "INFORMASI", "Error", "Pastikan semua data terisi!");
        } else {
            try {
                DatabaseController db = new DatabaseController();
                if (db.TambahTugas(idacc, nama, tanggal, waktu, kategori)){
                    db.tutup_cinta();
                }
            } catch (Exception e) {
                PesanMessage.tampilpesan(Alert.AlertType.ERROR, "INFORMASI", "Error", "Terjadi Kesalahan!");
            }
        }
    }

    private void loadcomboboxkategori(){
        ObservableList<String> kategori = FXCollections.observableArrayList();
        try {
            DatabaseController db = new DatabaseController();
            kategori = db.loadcomboboxkat();

            if (kategori.size() > 0) {
                combxkategori.setItems(kategori);
            }
            else{

            }
        }
        catch (Exception e) {
            System.out.println("blog");
        }
    }
}
