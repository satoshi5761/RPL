package prlbo.project.rpl.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class TambahKategoriController {

    public TextField categoryNameField;
    @FXML
    private TextField txtNamaKategori;

    @FXML
    private Button btnTambahKategori;

    private int idAccount;

    public void setIdAccount(int idAccount) {
        this.idAccount = idAccount;
    }

    @FXML
    public void initialize() {
        btnTambahKategori.setOnAction(e -> tambahKategori());
    }

    private void tambahKategori() {
        String namaKategori = txtNamaKategori.getText();

        if (namaKategori.isEmpty()) {
            showError("Nama kategori tidak boleh kosong!");
            return;
        }

        try {
            DatabaseController db = new DatabaseController();
            boolean sukses = db.TambahKategori(idAccount, namaKategori);

            if (sukses) {
                db.tutup_cinta();
                showSuccess("Kategori berhasil ditambahkan.");
            } else {
                showError("Gagal menambahkan kategori.");
            }
        } catch (Exception e) {
            showError("Terjadi kesalahan saat menambahkan kategori.");
        }
    }

    private void showError(String pesan) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Kesalahan");
        alert.setContentText(pesan);
        formatAlert(alert);
        alert.showAndWait();
    }

    private void showSuccess(String pesan) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sukses");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        formatAlert(alert);
        alert.showAndWait();
    }

    private void formatAlert(Alert alert) {
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/Asset/To-Do-List.png")));
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/Asset/Style.css").toExternalForm());
        dialogPane.getStyleClass().add("CustomNotif");
    }
}
