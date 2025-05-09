package prlbo.project.rpl.Controller;

import javafx.beans.property.SimpleStringProperty;
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
import prlbo.project.rpl.util.PesanMessage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    private int idacc;
    private ObservableList<ObservableList<String>> kategoriData = FXCollections.observableArrayList();

    public void set_idacc(int id) {
        this.idacc = id;
        AmbilData();
    }

    public void AmbilData() {
        // Fixed SQL query - added FROM clause
        String query = "SELECT id_kategori, namaKategori FROM kategori WHERE id_account = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:DMAC.db");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idacc);
            ResultSet rs = stmt.executeQuery();

            kategoriData.clear(); // Clear existing data before loading new

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs.getInt("id_kategori"))); // Store ID (hidden)
                row.add(rs.getString("namaKategori")); // Store category name
                kategoriData.add(row);
            }

            // Set up table columns
            NoColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(String.valueOf(kategoriData.indexOf(cellData.getValue()) + 1)));

            KategoriColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().get(1))); // Display category name

            KategoriTable.setItems(kategoriData);

        } catch (SQLException e) {
            e.printStackTrace();
            PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Database Error",
                    "Gagal Memuat Data", "Terjadi kesalahan saat memuat data kategori");
        }
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
            if (db.TambahKategori(idacc, kategori)) {
                PesanMessage.tampilpesan(Alert.AlertType.INFORMATION, "Sukses",
                        "Kategori Ditambahkan", "Kategori berhasil ditambahkan");

                AmbilData();
                categoryNameField.clear();
            } else {
                PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Database Error",
                        "Gagal Menyimpan", "Gagal menambahkan kategori");
            }
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
    public void handleCancelBtn(ActionEvent actionEvent) {
        closeWindow(actionEvent);
    }

    private void navigateToMain(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/main.fxml"));
            Parent root = loader.load();
            MainController mainController = loader.getController();
//            mainController.set_idacc(idacc);

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