package prlbo.project.rpl.Controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import prlbo.project.rpl.data.User;
import prlbo.project.rpl.util.PesanMessage;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EditKategoriController {
    @FXML
    public TextField categoryNameField;
    @FXML
    public TableView<ObservableList<String>> KategoriTable;
    @FXML
    public Label statusLabel;
    @FXML
    public TableColumn<ObservableList<String>, String> NoColumn;
    @FXML
    public TableColumn<ObservableList<String>, String> KategoriColumn;
    private boolean isEditCategory = false;
    private String kategoriLama = null;


    private int idacc;
    private ObservableList<ObservableList<String>> kategoriData = FXCollections.observableArrayList();

    public void initialize(URL location, ResourceBundle resources) {
        KategoriTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Catatan>() {
            public void changed(ObservableValue<?> observableValue) {
                if (observableValue.getValue() != null) {
                    selectedCatatan = observableValue.getValue();
                    txtFldJudul.setText(observableValue.getValue().getJudul());
                }
            }
        }
    }

    public void set_idacc(int id) {
        this.idacc = id;
        AmbilData();
    }

    public void AmbilData() {
        String query = "SELECT id_kategori, namaKategori FROM kategori WHERE id_account = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:DMAC.db");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idacc);
            ResultSet rs = stmt.executeQuery();

            kategoriData.clear();

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs.getInt("id_kategori")));
                row.add(rs.getString("namaKategori"));
                kategoriData.add(row);
            }

            // Set up table columns
            NoColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(String.valueOf(kategoriData.indexOf(cellData.getValue()) + 1)));

            KategoriColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().get(1)));

            KategoriTable.setItems(kategoriData);

        } catch (SQLException e) {
            e.printStackTrace();
            PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Database Error",
                    "Gagal Memuat Data", "Terjadi kesalahan saat memuat data kategori");
        }
    }
    public void editKategori(String kategori) {
        isEditCategory = true;
        kategoriLama = kategori;
        categoryNameField.setText(kategori);
    }


    @FXML
    public void SaveCategoryBtn(ActionEvent actionEvent) {
        String kategori = categoryNameField.getText().trim();
        if (kategori.isEmpty()) {
            PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Validasi",
                    "Data Kosong", "Nama kategori tidak boleh kosong!");
            return;
        }

        DatabaseController db = null;
        try {
            db = new DatabaseController();
            if (isEditCategory) {
                // Mode edit
                boolean sukses = db.EditKategori(idacc, kategoriLama, kategori);
                if (sukses) {
                    PesanMessage.tampilpesan(Alert.AlertType.INFORMATION, "Sukses",
                            "Kategori Diperbarui", "Kategori berhasil diperbarui");
                } else {
                    PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Gagal",
                            "Gagal Mengubah", "Perubahan gagal disimpan");
                }
                isEditCategory = false;
                kategoriLama = null;
            } else {
                // Mode tambah
                if (db.TambahKategori(idacc, kategori)) {
                    PesanMessage.tampilpesan(Alert.AlertType.INFORMATION, "Sukses",
                            "Kategori Ditambahkan", "Kategori berhasil ditambahkan");
                } else {
                    PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Gagal",
                            "Gagal Menyimpan", "Kategori gagal ditambahkan");
                }
            }

            AmbilData();
            categoryNameField.clear();

        } catch (Exception e) {
            e.printStackTrace();
            PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Error",
                    "Terjadi Kesalahan", "Terjadi kesalahan saat menyimpan kategori");
        } finally {
            if (db != null) {
                db.tutup_cinta();
            }
        }
    }



    @FXML
    public void BackBtn(ActionEvent actionEvent) {
        navigateToMain(actionEvent);
    }

    @FXML
    public void hapusBtn(ActionEvent actionEvent) {
        try {
            DatabaseController db = new DatabaseController();
            int idkategori = 0;

            ObservableList selectedrow = (ObservableList) KategoriTable.getSelectionModel().getSelectedItem();
            if (selectedrow != null) {
                idkategori = Integer.parseInt((String) selectedrow.get(0));
                db.HapusKategori(idacc, idkategori);
            } else {
                PesanMessage.tampilpesan(Alert.AlertType.ERROR, "INFORMASI", "Error", "Belum ada data yang dipilih.");
            }
            AmbilData();
        }
        catch (Exception e) {
            e.printStackTrace();
            PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Error",
                    "Gagal Hapus", "Gagal hapus kategori");
        }
    }

    private void navigateToMain(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/main.fxml"));
            Parent root = loader.load();
            MainController mainController = loader.getController();
            Stage stage = getStage(event);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Navigation Error",
                    "Gagal Kembali", "Tidak dapat kembali ke halaman utama");
        }
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = getStage(event);
        stage.close();
    }

    private Stage getStage(ActionEvent e) {
        return (Stage) ((Node) e.getSource()).getScene().getWindow();
    }
}