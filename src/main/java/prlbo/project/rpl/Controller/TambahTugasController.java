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
import javafx.util.Duration;
import prlbo.project.rpl.util.PesanMessage;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.io.IOException;
import java.time.LocalDate;

public class TambahTugasController {
    @FXML
    private Button btntambah;
    @FXML
    private Label labeljudul;

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
    public void setjudul(String s) {
        labeljudul.setText(s);
        btntambah.setText(s);
    }

    public void set_nama (String nama){
        this.nama = nama;
    }

    public void set_duedate (String duedate){
        this.duedate = duedate;
//        loadcomboboxkategori();

    }

    public void set_kategori (String kategori){
        this.kategori = kategori;
        isEdit = true;
        loaddatatugas();


//        ObservableList<String> kategorilist = FXCollections.observableArrayList();
//        try {
//            DatabaseController db = new DatabaseController();
//            kategorilist = db.loadcomboboxkat(idacc);
//            if (kategorilist.size() <= 0) {
//                db.TambahKategori(idacc, "Tugas");
//                kategorilist = db.loadcomboboxkat(idacc);
//                combxkategori.setItems(kategorilist);
//
//            }
//        } catch (Exception e) {
//            System.out.println("gagal");
//        }
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
        db.tutup_database();
    }

    @FXML
    void addTugas(ActionEvent event) throws IOException {
        String nama1 = txtnama.getText();
        LocalDate tanggal = dateduedate.getValue();
        String kategori1 = combxkategori.getValue();
        if (nama1.isEmpty() || tanggal == null || kategori1 == null) {
            PesanMessage.tampilpesan(Alert.AlertType.ERROR, "INFORMASI", "Error", "Pastikan semua data terisi!");
        }
        else {
            try {
                DatabaseController db = new DatabaseController();
                if (isEdit) {
                    if(tanggal.isBefore(LocalDate.now())){
                        PesanMessage.tampilpesan(Alert.AlertType.ERROR, "INFORMASI", "Error", "Tanggal sudah berakhir!");
                    }
                    else {
                        if (db.EditTugas(idacc, nama, duedate, kategori, nama1, String.valueOf(tanggal), kategori1)) {
                            FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/main.fxml"));
                            Parent root = fxml_load.load();
                            MainController main = fxml_load.getController();
                            Stage currStage = getStage(event);
                            currStage.setScene(new Scene(root));
                            currStage.show();
                            db.tutup_database();
                            TrayNotification tray = new TrayNotification();
                            System.out.println("updated");
                            tray.setTitle("Tugas Berhasil Di Update!");
                            tray.setNotificationType(NotificationType.SUCCESS);
                            tray.showAndDismiss(Duration.seconds(0.5));
                        } else {
                            System.out.println("Gagal update");
                        }
                    }
                }
                else {
                    if(tanggal.isBefore(LocalDate.now())){
                        PesanMessage.tampilpesan(Alert.AlertType.ERROR, "INFORMASI", "Error", "Tanggal sudah berakhir!");
                    }
                    else {
                        if (db.TambahTugas(idacc, nama1, tanggal, kategori1)) {
                            FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/main.fxml"));
                            Parent root = fxml_load.load();
                            MainController main = fxml_load.getController();
                            Stage currStage = getStage(event);
                            currStage.setScene(new Scene(root));
                            currStage.show();

                            // Notifikasi sukses

                            TrayNotification tray = new TrayNotification();
                            tray.setTitle("Tugas Berhasil Ditambahkan!");
                            tray.setMessage("Tugas " + nama1 + " dengan kategori " + kategori1 + " telah ditambahkan.");
                            tray.setNotificationType(NotificationType.SUCCESS);
                            tray.showAndDismiss(Duration.seconds(3));
                        }
                    }
                    db.tutup_database();
            }
            }catch (Exception e) {
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
                db.TambahKategori(idacc, "Tugas");
                kategori = db.loadcomboboxkat(idacc);
                combxkategori.setItems(kategori);
            }
            db.tutup_database();
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
