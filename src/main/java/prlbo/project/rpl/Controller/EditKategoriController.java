package prlbo.project.rpl.Controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable; // Import Initializable
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
// Assuming User class is not directly used in this controller based on provided code.
// import prlbo.project.rpl.data.User;
import prlbo.project.rpl.util.PesanMessage;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EditKategoriController implements Initializable { // Implement Initializable
    @FXML
    public TextField categoryNameField;
    @FXML
    public TableView<ObservableList<String>> KategoriTable;
    @FXML
    public Label statusLabel; // This label is declared but not used in the original logic.
    @FXML
    public TableColumn<ObservableList<String>, String> NoColumn;
    @FXML
    public TableColumn<ObservableList<String>, String> KategoriColumn;

    private boolean isEditCategory = false;
    private String kategoriLama = null;
    private int idacc;
    private ObservableList<ObservableList<String>> kategoriData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (KategoriTable == null) {
            System.err.println("EditKategoriController: KategoriTable is NULL in initialize(). Check FXML fx:id.");
            return;
        }
        if (categoryNameField == null) {
            System.err.println("EditKategoriController: categoryNameField is NULL in initialize(). Check FXML fx:id.");
        }

        KategoriTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    System.out.println("EditKategoriController: Table selection changed. newValue = " + newValue);

                    if (newValue != null) {
                        System.out.println("EditKategoriController: newValue is not null. Size: " + newValue.size());

                        if (newValue.size() > 1) {
                            String selectedKategori = newValue.get(1); // index 1 is the category name
                            System.out.println("EditKategoriController: Extracted category name (newValue.get(1)): '" + selectedKategori + "'");

                            if (categoryNameField != null) {
                                categoryNameField.setText(selectedKategori != null ? selectedKategori : "");
                            } else {
                                System.err.println("EditKategoriController: categoryNameField is NULL inside listener when setting text!");
                            }
                            editKategori(selectedKategori); // This also sets isEditCategory and kategoriLama
                        } else {
                            System.err.println("EditKategoriController: Selected row (newValue) has size " + newValue.size() + ", but expected at least 2 elements (id, name).");
                            clearFormAndSelection(); // Clear form if data is not as expected
                        }
                    } else {
                        // No item is selected (e.g., selection cleared or table empty)
                        System.out.println("EditKategoriController: newValue is null (item deselected or table empty). Clearing form.");
                        clearFormAndSelection();
                    }
                }
        );
        KategoriTable.setPlaceholder(new Label("Tidak ada kategori untuk ditampilkan."));
    }

    public void set_idacc(int id) {
        this.idacc = id;
        AmbilData();
    }

    public void AmbilData() {
        String query = "SELECT id_kategori, namaKategori FROM kategori WHERE id_account = ?";
        kategoriData.clear(); // Clear existing data before loading new

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:DMAC.db");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idacc);
            ResultSet rs = stmt.executeQuery();

            int rowNum = 1;
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs.getInt("id_kategori"))); // Column 0: id_kategori
                row.add(rs.getString("namaKategori"));          // Column 1: namaKategori
                kategoriData.add(row);
            }
            NoColumn.setCellValueFactory(cellDataFeatures -> {
                int index = KategoriTable.getItems().indexOf(cellDataFeatures.getValue());
                return new SimpleStringProperty(String.valueOf(index + 1));
            });


            KategoriColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().get(1))); // namaKategori is at index 1

            KategoriTable.setItems(kategoriData);

        } catch (SQLException e) {
            e.printStackTrace();
            PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Database Error",
                    "Gagal Memuat Data", "Terjadi kesalahan saat memuat data kategori: " + e.getMessage());
        }
    }

    public void editKategori(String kategori) {
        isEditCategory = true;
        kategoriLama = kategori;
        if (categoryNameField != null) {
            categoryNameField.setText(kategori != null ? kategori : "");
        }
    }

    private void clearFormAndSelection() {
        if (categoryNameField != null) {
            categoryNameField.clear();
        }
        isEditCategory = false;
        kategoriLama = null;
    }


    @FXML
    public void SaveCategoryBtn(ActionEvent actionEvent) {
        String kategoriBaru = categoryNameField.getText().trim();
        if (kategoriBaru.isEmpty()) {
            PesanMessage.tampilpesan(Alert.AlertType.WARNING, "Validasi",
                    "Data Kosong", "Nama kategori tidak boleh kosong!");
            categoryNameField.requestFocus();
            return;
        }

        DatabaseController db = null;
        try {
            db = new DatabaseController();
            if (isEditCategory) {
                if (kategoriLama == null) {
                    PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Error",
                            "Kesalahan Edit", "Kategori lama tidak teridentifikasi. Silakan pilih lagi.");
                    clearFormAndSelection();
                    KategoriTable.getSelectionModel().clearSelection();
                    return;
                }
                boolean sukses = db.EditKategori(idacc, kategoriLama, kategoriBaru);
                if (sukses) {
                    PesanMessage.tampilpesan(Alert.AlertType.INFORMATION, "Sukses",
                            "Kategori Diperbarui", "Kategori '" + kategoriLama + "' berhasil diperbarui menjadi '" + kategoriBaru + "'.");
                } else {
                    PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Gagal",
                            "Gagal Mengubah", "Perubahan gagal disimpan. Pastikan nama kategori baru unik jika diperlukan.");
                }
            } else {
                // Mode tambah
                if (db.TambahKategori(idacc, kategoriBaru)) {
                    PesanMessage.tampilpesan(Alert.AlertType.INFORMATION, "Sukses",
                            "Kategori Ditambahkan", "Kategori '" + kategoriBaru + "' berhasil ditambahkan.");
                } else {
                    PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Gagal",
                            "Gagal Menyimpan", "Kategori gagal ditambahkan. Mungkin nama kategori sudah ada.");
                }
            }

            AmbilData();
            clearFormAndSelection();
            KategoriTable.getSelectionModel().clearSelection();

        } catch (Exception e) {
            e.printStackTrace();
            PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Error Sistem",
                    "Terjadi Kesalahan", "Terjadi kesalahan sistem saat menyimpan kategori: " + e.getMessage());
        } finally {
            if (db != null) {
                db.tutup_cinta();
            }
        }
    }


    @FXML
    public void hapusBtn(ActionEvent actionEvent) {
        ObservableList<String> selectedRow = KategoriTable.getSelectionModel().getSelectedItem();

        if (selectedRow == null) {
            PesanMessage.tampilpesan(Alert.AlertType.WARNING, "Peringatan", "Tidak Ada Pilihan", "Belum ada data kategori yang dipilih untuk dihapus.");
            return;
        }

        String namaKategoriYangDihapus = selectedRow.get(1); // namaKategori is at index 1
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Konfirmasi Hapus");
        confirmationDialog.setHeaderText("Hapus Kategori: " + namaKategoriYangDihapus);
        confirmationDialog.setContentText("Apakah Anda yakin ingin menghapus kategori ini?");

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                DatabaseController db = null;
                try {
                    db = new DatabaseController();
                    int idkategori = Integer.parseInt(selectedRow.get(0)); // id_kategori is at index 0

                    boolean success = db.HapusKategori(idacc, idkategori); // Assume HapusKategori returns boolean for success
                    if (success) {
                        PesanMessage.tampilpesan(Alert.AlertType.INFORMATION, "Sukses",
                                "Kategori Dihapus", "Kategori '" + namaKategoriYangDihapus + "' berhasil dihapus.");
                        AmbilData(); // Refresh data
                        clearFormAndSelection(); // Clear form and selection
                        KategoriTable.getSelectionModel().clearSelection();
                    } else {
                        PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Gagal",
                                "Gagal Hapus", "Gagal menghapus kategori '" + namaKategoriYangDihapus + "'.");
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Error Data",
                            "Format ID Salah", "ID Kategori tidak valid untuk baris yang dipilih.");
                } catch (Exception e) {
                    e.printStackTrace();
                    PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Error Sistem",
                            "Gagal Hapus", "Terjadi kesalahan sistem saat menghapus kategori: " + e.getMessage());
                } finally {
                    if (db != null) {
                        db.tutup_cinta(); // Ensure this method exists and closes resources
                    }
                }
            }
        });
    }

    @FXML
    public void BackBtn(ActionEvent actionEvent) {
        navigateToMain(actionEvent);
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
                    "Gagal Kembali", "Tidak dapat kembali ke halaman utama: " + e.getMessage());
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