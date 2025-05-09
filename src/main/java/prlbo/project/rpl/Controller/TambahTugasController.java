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
    private DatePicker dateduedate;

    @FXML
    private TextField txtnama;

    @FXML
    private Button btnKembali;

    private int idacc;
    private String nama;
    private String duedate;
    private String kategori;
    private boolean isEdit = false;

    public void set_idacc(int id) {
        idacc = id;
        loadcomboboxkategori();
    }

    public void set_nama (String nama){
        this.nama = nama;
    }

    public void set_duedate (String duedate){
        this.duedate = duedate;

    }

    public void set_kategori (String kategori){
        this.kategori = kategori;
        isEdit = true;
        loaddatatugas();
    }


    public void initialize() {

    }


    Stage getStage(ActionEvent e) {
        /* Mendapatkan Stage dari node objek e yang di lakukan action*/
        return (Stage) (((Node) e.getSource()).getScene().getWindow());
    }

    @FXML
    void Kembali(ActionEvent event) throws Exception {
        DatabaseController db = new DatabaseController();
        FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/main.fxml"));
        Parent root = fxml_load.load();
        MainController main = fxml_load.getController();
        Stage currStage = getStage(event);
        currStage.setScene(new Scene(root));
        currStage.show();
        db.tutup_cinta();
    }

    @FXML
    void addTugas(ActionEvent event) throws IOException {
        String nama1 = txtnama.getText();
        LocalDate tanggal = dateduedate.getValue();
        String kategori1 = combxkategori.getValue();
        if (nama1.isEmpty() || tanggal == null) {
            PesanMessage.tampilpesan(Alert.AlertType.ERROR, "INFORMASI", "Error", "Pastikan semua data terisi!");
        } else {
            try {
                DatabaseController db = new DatabaseController();
                if (isEdit) {
                    if(db.EditTugas(idacc, nama, duedate, kategori, nama1, String.valueOf(tanggal), kategori1)){
                        FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/main.fxml"));
                        Parent root = fxml_load.load();
                        MainController main = fxml_load.getController();
                        Stage currStage = getStage(event);
                        currStage.setScene(new Scene(root));
                        currStage.show();
                        db.tutup_cinta();
                    }
                    else{
                        System.out.println("Gagal update");
                    }
                }
                if (db.TambahTugas(idacc, nama1, tanggal, kategori1)) {
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
            kategori = db.loadcomboboxkat(idacc);
            if (kategori.size() > 0) {
                combxkategori.setItems(kategori);
            } else {

            }
        } catch (Exception e) {
            System.out.println("gagal");
        }
    }

    private void loaddatatugas() {
        txtnama.setText(nama);
        dateduedate.setValue(LocalDate.parse(duedate));
        loadcomboboxkategori();
        combxkategori.setValue(kategori);
    }
}
