import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class ParkirController {

    @FXML
    private Button btnLaporan;

    @FXML
    private Label lblBigPlat;

    @FXML
    private Label lblHarga;

    @FXML
    private TableColumn<Kendaraan, String> lblHargaAkhr;

    @FXML
    private TextField lblInputPlat;

    @FXML
    private TableColumn<Kendaraan, String> lblJenisKendaraan;

    @FXML
    private Label lblJenisKendaraanDipilih;

    @FXML
    private TableColumn<Kendaraan, String> lblPlat;

    @FXML
    private Label lblRoda2;

    @FXML
    private Label lblRoda4;

    @FXML
    private Label lblRoda6;

    @FXML
    private TableColumn<Kendaraan, String> lblWaktuMasuk;

    @FXML
    private TableView<Kendaraan> tblKendaraan;

    private char roda;

    int[] store_roda = new int[7];

    private ObservableList<Kendaraan> dataKendaraan = FXCollections.observableArrayList();
    private ObservableList<Kendaraan> kendaraanKeluar = FXCollections.observableArrayList();

    @FXML
    void input_plat(ActionEvent e) throws Exception {

        String plat = lblInputPlat.getText();
        lblInputPlat.clear();

        String jenis = lblJenisKendaraanDipilih.getText();
        int roda = jenis.charAt(jenis.length() - 1) - '0';
        System.out.println(roda);
        store_roda[roda]++;

        count_roda(lblRoda2, roda, 2);
        count_roda(lblRoda4, roda, 4);
        count_roda(lblRoda6, roda, 6);

        insert_kendaraan(plat, roda);

        lblBigPlat.setText(plat);

        System.out.println(plat);

    }

    void count_roda(Label l, int r, Integer x) {
        l.setText("Roda " + x.toString() + " :" + ((Integer) store_roda[x]).toString());
    }

    void insert_kendaraan(String plat, int roda) {
        String jenis = "Kendaraan roda " + roda;
        String waktuMasuk = LocalTime.now().toString();
        String plat_nomor = plat;
        int harga = roda;

        Kendaraan k = new Kendaraan(plat_nomor, waktuMasuk, harga, jenis);
        dataKendaraan.add(k);
    }

    @FXML
    public void initialize() {
        lblInputPlat.requestFocus(); 

        lblInputPlat.setOnKeyPressed(x -> {
            if (x.getCode() == KeyCode.F1) {
                roda = '2';
            } else if (x.getCode() == KeyCode.F2) {
                roda = '4';
            } else if (x.getCode() == KeyCode.F3) {
                roda =  '6';
            }
            lblJenisKendaraanDipilih.setText("Jenis Kendaraan : " + roda);

            System.out.println(x);
        });

        lblJenisKendaraan.setCellValueFactory(new PropertyValueFactory<>("jenis"));
        lblPlat.setCellValueFactory(new PropertyValueFactory<>("platNomor"));
        lblHargaAkhr.setCellValueFactory(new PropertyValueFactory<>("harga"));
        lblWaktuMasuk.setCellValueFactory(new PropertyValueFactory<>("waktuMasuk"));

        tblKendaraan.setItems(dataKendaraan);

        tblKendaraan.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                Kendaraan selected = tblKendaraan.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    selected.setWaktuKeluar(LocalTime.now().toString());

                    int r = selected.getHarga(); 
                    store_roda[r]--;
                    count_roda(lblRoda2, r, 2);
                    count_roda(lblRoda4, r, 4);
                    count_roda(lblRoda6, r, 6);
                    kendaraanKeluar.add(selected);
                    dataKendaraan.remove(selected);
                }
            }
        });
        
    }

    Stage getStage(ActionEvent e) {
        /* Mendapatkan Stage dari node objek e yang di lakukan action*/ 
        return (Stage) ( ((Node) e.getSource()).getScene().getWindow() );
    }

    @FXML
    public void ClickedLaporan(ActionEvent e) throws IOException {
        /* User yang sudah memiliki akun pindah ke Login.fxml*/
        System.out.println("Login clicked");

        FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("Laporan.fxml"));
        Parent root = fxml_load.load();

        LaporanController laporan = fxml_load.getController();

        int total = 0;
        for (Kendaraan k : kendaraanKeluar) {
            total += k.getHarga();
        }
        total = total * 1000;

        laporan.transfer_data(kendaraanKeluar, total);

        
        Stage currStage = getStage(e);
        currStage.setScene(new Scene(root));
        currStage.show();

    }

}
