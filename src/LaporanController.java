import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class LaporanController {

    @FXML
    private TableColumn<Kendaraan, String> lblHarga;

    @FXML
    private TableColumn<Kendaraan, String> lblJenisKendaraan;

    @FXML
    private TableColumn<Kendaraan, String> lblNoPLat;

    @FXML
    private Label lblPendapatan;

    @FXML
    private TableColumn<Kendaraan, String> lblWaktuKeluar;

    @FXML
    private TableColumn<Kendaraan, String> lblWaktuMasuk;

    @FXML
    private TableView<Kendaraan> tblLaporan;

    // ObservableList<Kendaraan> dataKendaraan = FXCollections.observableArrayList();

    public void transfer_data(ObservableList<Kendaraan> data, int pendapatan) {
        tblLaporan.setItems(data);
        lblPendapatan.setText("Perkiraan Pendapatan: " + pendapatan);

       lblJenisKendaraan.setCellValueFactory(new PropertyValueFactory<>("jenis"));
        lblNoPLat.setCellValueFactory(new PropertyValueFactory<>("platNomor"));
        lblHarga.setCellValueFactory(new PropertyValueFactory<>("harga"));
        lblWaktuMasuk.setCellValueFactory(new PropertyValueFactory<>("waktuMasuk")); 
        lblWaktuKeluar.setCellValueFactory(new PropertyValueFactory<>("waktuKeluar")); 
    }

}
