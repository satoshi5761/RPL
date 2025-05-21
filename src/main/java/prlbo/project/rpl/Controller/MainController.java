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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import org.w3c.dom.ls.LSOutput;
import prlbo.project.rpl.Manager.UserManager;
import prlbo.project.rpl.data.User;
import prlbo.project.rpl.util.PesanMessage;

import javax.swing.*;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

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
    private Button btnHistoryTugas;

    @FXML
    private Button btnPieChart;

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

    @FXML
    public void initialize() {
        User user = UserManager.currentUser;
        String name = "";
        int id = 0;
        if (user != null) {
            name = user.getUsername();
            id = user.getId();
        }
        set_usser(name);
        set_idacc(id);
        searchBox.textProperty().addListener((observable, oldValue, newValue)
                -> {AmbilData(newValue);});
    }

    public void set_usser(String user) {
        user = user;
        lblNama.setText(user);
    }
    public void set_idacc(int id) {
        idacc = id;
        AmbilData("");
    }

    public void AmbilData(String search) {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        String query = "SELECT tugas.namaTugas, tugas.dueDate, kategori.namaKategori FROM tugas INNER JOIN kategori ON tugas.id_kategori = kategori.id_kategori WHERE tugas.id_account = ?";
        if (search!= null && !search.isEmpty()) {
            query += " AND (LOWER(tugas.namaTugas) LIKE ? OR LOWER(kategori.namaKategori) LIKE ?)";
        }
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:DMAC.db");
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idacc);
            if (search!= null && !search.isEmpty()) {
                stmt.setString(2, "%" + search.toLowerCase() + "%");
                stmt.setString(3, "%" + search.toLowerCase() + "%");
            }
            ResultSet rs = stmt.executeQuery();
            SimpleDateFormat masuk = new SimpleDateFormat("yyyy-MM-dd");
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(rs.getString("namaTugas"));
                row.add(rs.getString("namaKategori"));
                String ambil = rs.getString("dueDate");
                String hasil;
                if(ambil.contains("-")){
                    hasil = ambil;
                } else {
                    long time = Long.parseLong(ambil);
                    hasil = masuk.format(time);
                }
                row.add(hasil);
                data.add(row);
            }
            colNo.setCellValueFactory(
                    cellData ->
                            new SimpleStringProperty(String.valueOf(tblTugas.getItems().indexOf(cellData.getValue()) + 1))
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
    void EditKategori(ActionEvent event) throws IOException {
        FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/EditKategori.fxml"));
        Parent root = fxml_load.load();
        Stage currStage = getStage(event);
        currStage.setScene(new Scene(root));
        currStage.show();
        EditKategoriController main = fxml_load.getController();
        int id = idacc;
        main.set_idacc(id);
    }

    @FXML
    void EditTugas(ActionEvent event) throws IOException {
        ObservableList selectedrow = (ObservableList) tblTugas.getSelectionModel().getSelectedItem();
        String namaold = "";
        String kategoriold = "";
        String duedateold = "";

        if (selectedrow != null) {
           namaold = selectedrow.get(0).toString();
           kategoriold = selectedrow.get(1).toString();
           duedateold = selectedrow.get(2).toString();
            FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/TambahTugas.fxml"));
            Parent root = fxml_load.load();
            Stage currStage = getStage(event);
            currStage.setScene(new Scene(root));
            currStage.show();
            TambahTugasController main = fxml_load.getController();
            int id = idacc;
            main.set_idacc(id);
            main.set_nama(namaold);
            main.set_duedate(duedateold);
            main.set_kategori(kategoriold);
        }
        else{
           PesanMessage.tampilpesan(Alert.AlertType.ERROR,"INFORMASI", "Error", "Belum ada data yang dipilih.");
        }
    }

    @FXML
    void Hapus(ActionEvent event) throws Exception {
        DatabaseController db = new DatabaseController();
        String nama = "";
        String duedate = "";
        String kategori = "";

        ObservableList selectedrow = (ObservableList) tblTugas.getSelectionModel().getSelectedItem();
        if (selectedrow != null) {
            nama = selectedrow.get(0).toString();
            kategori = selectedrow.get(1).toString();
            duedate = selectedrow.get(2).toString();
            db.HapusTugas(idacc, nama, duedate, kategori);
            AmbilData("");

           boolean tugas_selesai = PesanMessage.selesai_atau_tidak("Task Status",
                   "Apakah Anda sudah menyelesaikan tugas ini?",
                   "Pilih salah satu:");

           if (tugas_selesai) {
               boolean is_inserted = db.InsertTugasSelesai(idacc, kategori, nama, duedate);
               if (is_inserted) System.out.println("selesai ditambahkan");
               else System.out.println("tugas yang selesai gagal untuk ditambahkan");
           } else {
                boolean is_inserted = db.InsertTugasTidakSelesai(idacc, kategori, nama, duedate);
               if (is_inserted) System.out.println("selesai ditambahkan");
               else System.out.println("tugas yang tidak selesai gagal untuk ditambahkan");
           }


        }
        else{
            PesanMessage.tampilpesan(Alert.AlertType.ERROR,"INFORMASI", "Error", "Belum ada data yang dipilih.");
        }

        db.tutup_cinta();
    }

    @FXML
    void HistoryTugas(ActionEvent e) throws  Exception {

        boolean history_selesai_or_not = PesanMessage.selesai_atau_tidak("Lihat history tugas",
                "Task History",
                "Pilih salah satu:");

        if (history_selesai_or_not) {
            CompletedTugas(e, "CompletedTask.fxml");
        } else {
            CompletedTugas(e, "UncompletedTask.fxml");
        }

    }

    void CompletedTugas(ActionEvent event, String sfxml) throws Exception {
        FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/" + sfxml));
        Parent root = fxml_load.load();

        Object controller = fxml_load.getController();
        if (sfxml.startsWith("Complete")) {
            ((CompletedTaskController) controller).setIdacc(idacc);
            ((CompletedTaskController) controller).initialize();
        } else {
            ((UncompletedTaskController) controller).setIdacc(idacc);
            ((UncompletedTaskController) controller).initialize();
        }

        Stage curstage = getStage(event);
        curstage.setScene(new Scene(root));
        curstage.show();
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

    @FXML
    void switchPie(ActionEvent e) throws IOException {
        FXMLLoader fxml_load = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/PieChart.fxml"));
        Parent root = fxml_load.load();
        PieChartController main = fxml_load.getController();

        main.setIdacc(idacc);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initOwner(getStage(e));
        stage.initModality(Modality.WINDOW_MODAL);

        stage.showAndWait();
    }


//
    public void Search(){
        searchBox.getText();
    }

}
