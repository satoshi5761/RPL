package prlbo.project.rpl.Controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import prlbo.project.rpl.Manager.SessionManager;
import prlbo.project.rpl.Manager.UserManager; // Assuming this exists
import prlbo.project.rpl.data.User; // Assuming this exists
import prlbo.project.rpl.util.PesanMessage; // Kept for now, ideal would be direct Alert

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class MainController implements Initializable {

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
    private Button lblBersih; // This seems to be a Button acting as a clear button
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
    private Label lblSapa; // Not used in original initialize, but kept
    @FXML
    private TableView<ObservableList<String>> tblTugas;

    private int currentUserIdAcc;
    private User currentUser;
    private ObservableList<String> selectedTugas; // To store the selected row data

    // For Table Data and Filtering
    private ObservableList<ObservableList<String>> masterTugasList;
    private FilteredList<ObservableList<String>> filteredTugasList;

    private Connection connection;
    private final String DB_URL = "jdbc:sqlite:DMAC.db"; // Centralized DB URL

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User user = UserManager.getCurrentUser();
        if (SessionManager.getInstance().isLoggedIn() && user != null) {
            System.out.println("Logged in as: " + user.getUsername());
            currentUser = user;
        }

        if (currentUser != null) {
            lblNama.setText(currentUser.getUsername());
            currentUserIdAcc = currentUser.getId();
        } else {
            // Handle case where user is not logged in, perhaps redirect or show error
            lblNama.setText("Guest");
            currentUserIdAcc = -1; // Or some other indicator of no user
            PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Error", "User not found", "No logged-in user detected.");
        }

        establishConnection(); // Establish DB connection

        masterTugasList = FXCollections.observableArrayList();
        filteredTugasList = new FilteredList<>(masterTugasList, p -> true); // Show all initially
        tblTugas.setItems(filteredTugasList);

        searchBox.textProperty().addListener((observable, oldValue, newValue) ->
                filteredTugasList.setPredicate(createTugasPredicate(newValue))
        );

        // Configure table columns
        // Note: Using ObservableList<String> for rows is less ideal than a custom POJO (e.g., Tugas class)
        // If a Tugas class existed, PropertyValueFactory could be used.
        colNo.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(tblTugas.getItems().indexOf(cellData.getValue()) + 1))
        );
        colNama.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0)));
        colKategori.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1)));
        colTenggat.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2)));

        loadAllTugasData(); // Load initial data

        // Listener for table selection
        tblTugas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedTugas = newSelection;
                // If you had fields to populate for editing in this view, you'd do it here.
                // e.g., txtFieldNamaTugas.setText(selectedTugas.get(0));
            } else {
                selectedTugas = null;
            }
        });

        // Optional: Greet user based on time
        // LocalTime now = LocalTime.now();
        // if (now.isBefore(LocalTime.NOON)) lblSapa.setText("Selamat Pagi,");
        // else if (now.isBefore(LocalTime.of(18,0))) lblSapa.setText("Selamat Siang/Sore,");
        // else lblSapa.setText("Selamat Malam,");
    }

    private void establishConnection() {
        try {
            connection = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
            PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Database Error", "Connection Failed", "Could not connect to the database: " + e.getMessage());
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle database connection closure error
            }
        }
    }

    private Predicate<ObservableList<String>> createTugasPredicate(String searchText) {
        return tugasRow -> {
            if (searchText == null || searchText.isEmpty()) {
                return true; // No filter
            }
            return searchFindsTugas(tugasRow, searchText.toLowerCase());
        };
    }

    private boolean searchFindsTugas(ObservableList<String> tugasRow, String searchTextLower) {
        // Assuming columns: 0: namaTugas, 1: namaKategori, (2: dueDate - not typically searched as string directly unless formatted)
        String namaTugas = tugasRow.get(0);
        String namaKategori = tugasRow.get(1);
        // String dueDate = tugasRow.get(2); // If you want to search by date string

        return (namaTugas != null && namaTugas.toLowerCase().contains(searchTextLower)) ||
                (namaKategori != null && namaKategori.toLowerCase().contains(searchTextLower));
        //    || (dueDate != null && dueDate.toLowerCase().contains(searchTextLower));
    }

    private void loadAllTugasData() {
        if (currentUserIdAcc == -1 || connection == null) {
            masterTugasList.clear(); // Clear list if no user or no connection
            return;
        }

        masterTugasList.clear();
        String query = "SELECT tugas.namaTugas, tugas.dueDate, kategori.namaKategori FROM tugas INNER JOIN kategori ON tugas.id_kategori = kategori.id_kategori WHERE tugas.id_account = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, currentUserIdAcc);
            ResultSet rs = stmt.executeQuery();
            SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd"); // To parse date if stored as string

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(rs.getString("namaTugas"));
                row.add(rs.getString("namaKategori"));

                String dueDateStr = rs.getString("dueDate");
                String formattedDueDate;
                if (dueDateStr.contains("-")) { // Already yyyy-MM-dd
                    formattedDueDate = dueDateStr;
                } else { // Assuming it's a long (timestamp)
                    try {
                        long time = Long.parseLong(dueDateStr);
                        formattedDueDate = dbDateFormat.format(new java.util.Date(time));
                    } catch (NumberFormatException e) {
                        formattedDueDate = dueDateStr; // Fallback if not a long
                        System.err.println("Warning: Could not parse dueDate as long: " + dueDateStr);
                    }
                }
                row.add(formattedDueDate);
                masterTugasList.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Database Error", "Fetch Failed", "Could not retrieve tasks: " + e.getMessage());
        }
    }

    private Stage getStage(ActionEvent e) {
        return (Stage) ((Node) e.getSource()).getScene().getWindow();
    }

    @FXML
    void Bersih(ActionEvent event) { // Renamed from Bersih and follows naming convention
        searchBox.clear();
        tblTugas.getSelectionModel().clearSelection();
        selectedTugas = null;
        event.consume();
    }

    @FXML
    void EditKategori(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/EditKategori.fxml"));
        Parent root = fxmlLoader.load();
        EditKategoriController controller = fxmlLoader.getController();
        controller.set_idacc(currentUserIdAcc); // Pass necessary data

        Stage currentStage = getStage(event);
        currentStage.setScene(new Scene(root));
        // currentStage.setTitle("Edit Kategori"); // Optional: set title
        currentStage.show();
    }

    @FXML
    void EditTugas(ActionEvent event) throws IOException {
        if (selectedTugas == null) {
            PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Selection Error", "No Task Selected", "Please select a task to edit.");
            return;
        }

        String namaOld = selectedTugas.get(0);
        String kategoriOld = selectedTugas.get(1);
        String duedateOld = selectedTugas.get(2);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/TambahTugas.fxml")); // Assuming edit uses TambahTugas view
        Parent root = fxmlLoader.load();
        TambahTugasController controller = fxmlLoader.getController();
        controller.set_idacc(currentUserIdAcc);
        controller.set_nama(namaOld);
        controller.set_duedate(duedateOld);
        controller.set_kategori(kategoriOld);
        // You might want a specific method in TambahTugasController to signify it's an edit operation

        Stage currentStage = getStage(event);
        currentStage.setScene(new Scene(root));
        // currentStage.setTitle("Edit Tugas"); // Optional: set title
        currentStage.show();
    }

    @FXML
    void Hapus(ActionEvent event) { // Renamed from Hapus
        if (selectedTugas == null) {
            PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Selection Error", "No Task Selected", "Please select a task to delete.");
            return;
        }

        String namaTugas = selectedTugas.get(0);
        String kategoriTugas = selectedTugas.get(1);
        String duedateTugas = selectedTugas.get(2);

        // Confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this task?", ButtonType.YES, ButtonType.NO);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Task: " + namaTugas);
        decorateAlertDialog(confirmAlert); // Optional styling

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                boolean deleted = false;
                // Re-using DatabaseController logic as an example, though direct SQL is also fine
                // If DatabaseController is complex, it might be better to adapt its methods or bring logic here.
                // For now, let's assume direct SQL for simplicity like in DaftarCatatanController
                String deleteQuery = "DELETE FROM tugas WHERE id_account = ? AND namaTugas = ? AND dueDate = ? AND id_kategori = (SELECT id_kategori FROM kategori WHERE namaKategori = ? AND id_account = ?)";

                try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
                    pstmt.setInt(1, currentUserIdAcc);
                    pstmt.setString(2, namaTugas);
                    pstmt.setString(3, duedateTugas); // Assuming dueDate is stored as string in 'yyyy-MM-dd' or long
                    pstmt.setString(4, kategoriTugas);
                    pstmt.setInt(5, currentUserIdAcc); // For subquery

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        deleted = true;
                        masterTugasList.remove(selectedTugas); // Remove from the master list, FilteredList updates
                        tblTugas.getSelectionModel().clearSelection();
                        selectedTugas = null;
                        PesanMessage.tampilpesan(Alert.AlertType.INFORMATION, "Success", "Task Deleted", "The task has been successfully deleted.");

                        // Handle task completion status (similar to original)
                        handleTaskCompletionStatus(namaTugas, kategoriTugas, duedateTugas);

                    } else {
                        PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Deletion Failed", "Error", "Could not delete the task from the database.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Database Error", "Deletion Failed", "Error during task deletion: " + e.getMessage());
                }
            }
        });
    }

    private void handleTaskCompletionStatus(String namaTugas, String kategoriTugas, String duedateTugas) {
        // This logic is from your original Hapus method
        boolean tugas_selesai = PesanMessage.selesai_atau_tidak("Task Status",
                "Did you complete this task?",
                "Select one:");

        // Assuming DatabaseController is an external utility or you implement these methods here
        // For simplicity, let's imagine these methods use the 'connection' field.
        // Example: insertIntoHistoryTable(currentUserIdAcc, kategoriTugas, namaTugas, duedateTugas, tugas_selesai);

        String historyTable = tugas_selesai ? "tugas_selesai" : "tugas_tidak_selesai";
        String insertHistoryQuery = "INSERT INTO " + historyTable + " (id_account, id_kategori, namaTugas, dueDate) VALUES (?, (SELECT id_kategori FROM kategori WHERE namaKategori = ? AND id_account = ?), ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertHistoryQuery)) {
            pstmt.setInt(1, currentUserIdAcc);
            pstmt.setString(2, kategoriTugas);
            pstmt.setInt(3, currentUserIdAcc); // for subquery
            pstmt.setString(4, namaTugas);
            pstmt.setString(5, duedateTugas);

            int inserted = pstmt.executeUpdate();
            if (inserted > 0) {
                System.out.println("Task history (" + (tugas_selesai ? "completed" : "not completed") + ") added for: " + namaTugas);
            } else {
                System.out.println("Failed to add task history for: " + namaTugas);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Database Error", "History Update Failed", "Error updating task history: " + e.getMessage());
        }
    }


    @FXML
    void HistoryTugas(ActionEvent event) throws Exception {
        boolean viewCompleted = PesanMessage.selesai_atau_tidak("View Task History",
                "Which task history would you like to view?",
                "Choose one:"); // Assuming 'selesai_atau_tidak' returns true for 'Completed'

        String fxmlFile = viewCompleted ? "CompletedTask.fxml" : "UncompletedTask.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/" + fxmlFile));
        Parent root = fxmlLoader.load();

        // Pass data to the controller of the new view
        Object controller = fxmlLoader.getController();
        if (controller instanceof CompletedTaskController) {
            ((CompletedTaskController) controller).setIdacc(currentUserIdAcc);
            ((CompletedTaskController) controller).initialize(); // Or a specific method to load data
        } else if (controller instanceof UncompletedTaskController) {
            ((UncompletedTaskController) controller).setIdacc(currentUserIdAcc);
            ((UncompletedTaskController) controller).initialize(); // Or a specific method to load data
        }

        Stage currentStage = getStage(event);
        currentStage.setScene(new Scene(root));
        // currentStage.setTitle("Task History");
        currentStage.show();
    }


    @FXML
    void Logout(ActionEvent event) { // Renamed from Logout
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("Press YES to logout and return to the login screen.");
        decorateAlertDialog(alert); // Optional styling

        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                SessionManager.getInstance().logout();
                try {
                    // Assuming an Apps class or similar for navigation, like in DaftarCatatanController
                    // If not, use direct FXMLLoader
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/login.fxml"));
                    Parent loginRoot = fxmlLoader.load();
                    Stage currentStage = getStage(event);
                    currentStage.setScene(new Scene(loginRoot));
                    currentStage.setTitle("Login");
                    currentStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Navigation Error", "Logout Failed", "Could not load login screen: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    void Tambah(ActionEvent event) throws IOException { // Renamed from Tambah
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/TambahTugas.fxml"));
        Parent root = fxmlLoader.load();
        TambahTugasController controller = fxmlLoader.getController();
        controller.set_idacc(currentUserIdAcc); // Pass necessary data

        Stage currentStage = getStage(event);
        currentStage.setScene(new Scene(root));
        // currentStage.setTitle("Add New Task");
        currentStage.show();
    }

    @FXML
    void switchPie(ActionEvent event) throws IOException { // Renamed from switchPie
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/prlbo/project/rpl/PieChart.fxml"));
        Parent root = fxmlLoader.load();
        PieChartController controller = fxmlLoader.getController();
        controller.setIdacc(currentUserIdAcc); // Pass ID
        // controller.loadChartData(); // Call a method in PieChartController to populate the chart

        Stage pieStage = new Stage();
        pieStage.setTitle("Task Distribution Chart");
        pieStage.setScene(new Scene(root));
        pieStage.initOwner(getStage(event));
        pieStage.initModality(Modality.WINDOW_MODAL);
        pieStage.showAndWait();
    }

    private void decorateAlertDialog(Alert alert) {
        // Common styling for alerts, similar to PesanMessage if it applies CSS
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        try {
            // Assuming your icon is in resources/Asset
            URL iconUrl = getClass().getResource("/Asset/To-Do-List.png");
            if (iconUrl != null) {
                stage.getIcons().add(new Image(iconUrl.toExternalForm()));
            }
        } catch (Exception e) {
            System.err.println("Icon not found: /Asset/To-Do-List.png " + e.getMessage());
        }

        DialogPane dialogPane = alert.getDialogPane();
        try {
            URL cssUrl = getClass().getResource("/Asset/Style.css");
            if (cssUrl != null) {
                dialogPane.getStylesheets().add(cssUrl.toExternalForm());
                dialogPane.getStyleClass().add("CustomNotif"); // Or your specific style class
            }
        } catch (Exception e) {
            System.err.println("Stylesheet not found: /Asset/Style.css " + e.getMessage());
        }
    }
}