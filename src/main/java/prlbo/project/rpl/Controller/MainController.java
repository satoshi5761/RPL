package prlbo.project.rpl.Controller;

import javafx.beans.property.ReadOnlyStringWrapper;
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
import javafx.scene.image.Image;
import prlbo.project.rpl.util.PesanMessage;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainController {

    @FXML
    private Button btnEditKategori;

    @FXML
    private Button btnEditTugas;

    @FXML
    private Button btnHapus;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnTambah;

    @FXML
    private Button lblBersih;
    @FXML
    private TableColumn<ObservableList<String>, String> colKategori;

    @FXML
    private TableColumn<ObservableList<String>, String> colNama;

    @FXML
    private TableColumn<ObservableList<String>, String> colNo;

    @FXML
    private TableColumn<ObservableList<String>, String> colTenggat;
    @FXML
    private TextField searchBox;
    @FXML
    private Label lblNama;

    @FXML
    private Label lblSapa;

    @FXML
    private TableView<ObservableList<String>> tblTugas;

    private int idacc;
    private int user;
    public void set_usser(String user) {
        user = user;
        lblNama.setText(user);
    }
    public void set_idacc(int id) {
        idacc = id;
        AmbilData();
    }

    public void AmbilData() {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
//        String query = "SELECT tugas.namaTugas, tugas.id_kategori, tugas.dueDate, kategori.namaKategori FROM tugas NATURAL JOIN kategori WHERE tugas.id_account = ?";
        String query = "SELECT tugas.namaTugas, tugas.dueDate, kategori.namaKategori FROM tugas INNER JOIN kategori ON tugas.id_kategori = kategori.id_kategori WHERE tugas.id_account = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:DMAC.db");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idacc);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(rs.getString("namaTugas"));
                row.add(rs.getString("namaKategori"));
                row.add(rs.getString("dueDate"));
                data.add(row);
            }
            colNo.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(tblTugas.getItems().indexOf(cellData.getValue()) + 1))
            );
            colNama.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0))
            );
            colKategori.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1))
            );
            colTenggat.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2))
            );
            tblTugas.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    Stage getStage(ActionEvent e) {
        /* Mendapatkan Stage dari node objek e yang di lakukan action*/
        return (Stage) ( ((Node) e.getSource()).getScene().getWindow() );
    }

    @FXML
    void Bersih(ActionEvent event) {
        searchBox.clear();
    }
    @FXML
    void EditKategori(ActionEvent event) {

    }

    @FXML
    void EditTugas(ActionEvent event) throws IOException {
        FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/TambahTugas.fxml"));
        Parent root = fxml_load.load();
        Stage currStage = getStage(event);
        currStage.setScene(new Scene(root));
        currStage.show();
        TambahTugasController main = fxml_load.getController();
        int id = idacc;
        main.set_idacc(id);
    }

    @FXML
    void Hapus(ActionEvent event) throws Exception {
        ///Seh Durung Dadi
        if (tblTugas.getSelectionModel().getSelectedItem() != null) {
            ObservableList<String> selectedRow = tblTugas.getSelectionModel().getSelectedItem();
            String namaTugas = selectedRow.get(0);
            DatabaseController db = new DatabaseController();
            db.HapusTugas(namaTugas);
            AmbilData();
        }
//        if(tblTugas.getSelectionModel().getSelectedItem() != null) {
//            DatabaseController db = new DatabaseController();
//            String query = "SELECT namaTugas FROM tugas WHERE id_account = ?";
//            Connection conn = DriverManager.getConnection("jdbc:sqlite:DMAC.db");
//            PreparedStatement stmt = conn.prepareStatement(query);
//            stmt.setInt(1, idacc);
//            ResultSet rs = stmt.executeQuery();
//            try {
//                if (rs.next()) {
//                    String nama = rs.getString("namaTugas");
//                    System.out.println(nama);
//                    FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/main.fxml"));
//                    Parent root = fxml_load.load();
//                    Stage currStage = getStage(event);
//                    currStage.setScene(new Scene(root));
//                    currStage.show();
//            }
//            }catch (SQLException e) {
//                System.out.println("Gagal ambil nama tugas");
//                e.printStackTrace();
//            }
//        }
    }

    @FXML
    void Logout(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Logout");
        alert.setHeaderText("Apakah ingin Logout?");
        alert.setContentText("Klik YES untuk lanjutkan");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(PesanMessage.class.getResourceAsStream("/Asset/To-Do-List.png")));
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(PesanMessage.class.getResource("/Asset/Style.css").toExternalForm());
        dialogPane.getStyleClass().add("CustomNotif");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/login.fxml"));
                    Parent root = fxml_load.load();
                    Stage currStage = getStage(event);
                    currStage.setScene(new Scene(root));
                    currStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    void Tambah(ActionEvent event) throws IOException {
        FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/TambahTugas.fxml"));
        Parent root = fxml_load.load();
        Stage currStage = getStage(event);
        currStage.setScene(new Scene(root));
        currStage.show();
        TambahTugasController main = fxml_load.getController();
        int id = idacc;
        main.set_idacc(id);
    }

}
