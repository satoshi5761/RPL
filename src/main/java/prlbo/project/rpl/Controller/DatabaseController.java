package prlbo.project.rpl.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import prlbo.project.rpl.util.HashFunction;
import prlbo.project.rpl.util.PesanMessage;
import java.sql.*;
import java.time.LocalDate;

public class DatabaseController {
    private Connection con;
    DatabaseController() throws Exception {
        this.con = DriverManager.getConnection("jdbc:sqlite:DMAC.db");
        System.out.println("DB connected");
    }

    public boolean userValid(String username) {
        boolean valid = false;
        String query = "SELECT * FROM account WHERE username = ?";
        try {
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            valid = rs.next();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return valid;
    }

    public int forgot(String username, String password) {
        password = HashFunction.getHash(password);
        if (!userValid(username)) {
            return -1;
        }
        String query = "UPDATE account SET password=? WHERE username=?";
        try {
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, password);
            stmt.setString(2, username);
            stmt.executeUpdate();
            System.out.println("Ubah Password berhasil");
            return 1;
        } catch (SQLException e) {
            System.out.println("Gagal Mengubah Password");
            return 0;
        }
    }

    public boolean register(String username, String password) {
        password = HashFunction.getHash(password);
        String query = "INSERT INTO account (username, password) VALUES (?, ?);";
        try {
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            System.out.println("Sukses Registrasi User");
            return true;
        } catch (SQLException e) {
            System.out.println("Presumably, UNIQUE entry condition has been breached");
            /* Note: NEED TO HANDLE THIS ON JAVAFX GUI POP UP */
            return false;
        }
    }

    public boolean login(String username, String passwd) throws SQLException {
        passwd = HashFunction.getHash(passwd);
        String query = "SELECT COUNT(*) FROM account WHERE username=? AND password=?;";

        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1, username);
        stmt.setString(2, passwd);

        ResultSet res = stmt.executeQuery();
        return res.getInt(1) >= 1;
    }

    public void tutup_database() {
        try {
            if (!con.isClosed()) {
                con.close();
                System.out.println("DB closed\n");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public ObservableList<String> loadcomboboxkat(int id) {
        ObservableList<String> kategori = FXCollections.observableArrayList();
        String query = "SELECT * FROM kategori WHERE id_account = ?;";
        try {
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                kategori.add(rs.getString("namaKategori"));
            }
        } catch (Exception e) {
            System.out.println("Gagal Ambil Kategori.");
        }
        return kategori;
    }

    public boolean TambahTugas(int id, String nama, LocalDate duedate, String kategori) {
        String query1 = "SELECT * FROM tugas WHERE id_account = ?;";
        String query = "INSERT INTO tugas (id_account, id_kategori, namaTugas, dueDate) VALUES (?, ?, ?, ?);";
        int idkategori = getidkategori(id, kategori);
        try {
            PreparedStatement stmt1 = con.prepareStatement(query1);
            stmt1.setInt(1, id);
            ResultSet rs = stmt1.executeQuery();
            while (rs.next()) {
                if(nama.equalsIgnoreCase(rs.getString("namaTugas")) &&
                        duedate.toString().equals(rs.getString("dueDate")) &&
                        idkategori == (rs.getInt("id_kategori"))){
                    PesanMessage.tampilpesan(Alert.AlertType.ERROR, "INFORMASI", "Error", "Tugas Sudah ada!");
                    return false;
                }
            }

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.setInt(2, idkategori);
            stmt.setString(3, nama);
            stmt.setString(4, duedate.toString());
            stmt.executeUpdate();
            System.out.println("Tugas berhasil ditambahkan.");
            return true;

        } catch (SQLException e) {
            System.out.println("Tugas gagal ditambahkan.");
            return false;
        }
    }

    public boolean TambahKategori(int accountId, String kategoriName) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM kategori WHERE namaKategori = ? AND id_account = ?";
        try {
            PreparedStatement stmt = con.prepareStatement(checkQuery);
            stmt.setString(1, kategoriName);
            stmt.setInt(2, accountId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Kategori dengan Nama " + kategoriName + " sudah ada");
                return false;
            }
            String insertQuery = "INSERT INTO kategori (namaKategori, id_account) VALUES (?, ?)";
            PreparedStatement stmt2 = con.prepareStatement(insertQuery);
            stmt2.setString(1, kategoriName);
            stmt2.setInt(2, accountId);
            int efektambah = stmt2.executeUpdate();
            if (efektambah > 0) {
                return true;
            } else {
                return false;
            }
            } catch (SQLException e) {
                System.err.println("Error menambahkan kategori: " + e.getMessage());
                throw e; // Re-throw the exception for proper handling in the caller
            }
        }

    public boolean EditTugas(int id, String namaold, String duedateold, String kategoriold, String nama, String duedate, String kategori) throws SQLException {
        String query = "UPDATE tugas SET id_kategori = ?, namaTugas = ?, dueDate = ? WHERE " +
                "id_account = ? AND id_kategori = ? AND namaTugas = ? AND dueDate = ?;";
        int idkategori = getidkategori(id, kategori);
        int idkategoriold = getidkategori(id, kategoriold);
        try {
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, idkategori);
            stmt.setString(2, nama);
            stmt.setString(3, duedate);
            stmt.setInt(4, id);
            stmt.setInt(5, idkategoriold);
            stmt.setString(6, namaold);
            stmt.setString(7, duedateold);

            stmt.executeUpdate();
            System.out.println("Tugas berhasil diedit.");
            return true;
        } catch (SQLException e) {
            System.out.println("Tugas gagal diedit.");
            return false;
        }
    }

    public boolean HapusTugas(int id, String nama, String duedate, String kategori) {
        String query = "DELETE FROM tugas WHERE id_account = ? AND id_kategori = ? AND namaTugas = ? AND dueDate = ?;";
        try {
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.setInt(2, getidkategori(id, kategori));
            stmt.setString(3, nama);
            stmt.setString(4, duedate);


            stmt.executeUpdate();
            System.out.println("Tugas berhasil dihapus.");
            return true;
        } catch (SQLException e) {
            System.out.println("Tugas gagal dihapus.");
            return false;
        }
    }

    public boolean EditKategori(int idacc, String kategoriLama, String kategoriBaru) {
        String sql = "UPDATE kategori SET namaKategori = ? WHERE id_account = ? AND namaKategori = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, kategoriBaru);
            stmt.setInt(2, idacc);
            stmt.setString(3, kategoriLama);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    public int getidkategori(int id, String kategori) {
        String query = "SELECT * FROM kategori WHERE id_account = ? AND namaKategori = ?;";
        int idkategori = 0;

        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, id);
            statement.setString(2, kategori);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                idkategori = rs.getInt("id_kategori");
            }
        }
        catch (SQLException e) {
            System.out.println("Ada error");
        }
        return idkategori;
    }

    public boolean HapusKategori(int id, int idkategori) {
        String query = "DELETE FROM kategori WHERE id_kategori = ? AND id_account = ?;";
        try {
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, idkategori);
            stmt.setInt(2, id);
            if(!cektugas(id, idkategori)) {
                stmt.executeUpdate();
                PesanMessage.tampilpesan(Alert.AlertType.INFORMATION, "Hapus Kategori",
                        "Berhasil hapus", "Kategori Terhapus!");
            }
            else{
                PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Error",
                        "Gagal hapus", "Terdapat tugas dalam kategori tersebut!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            PesanMessage.tampilpesan(Alert.AlertType.ERROR, "Hapus Kategori",
                    "Gagal hapus", "Tidak dapat menghapus kategori.");

        }
        return false;
    }

    public boolean cektugas(int id, int idkategori) {
        String query = "SELECT * FROM tugas WHERE id_kategori = ? AND id_account = ?;";
        try {
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, idkategori);
            stmt.setInt(2, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                return true;
            }
            else{
                return false;
            }
        } catch (SQLException e) {
            System.out.println("biar tau kalo gagal");
            return false;
        }
    }

    public boolean InsertTugasSelesai(int id, String namaKategori, String namaTugas, String dueDate) {
        String query = "INSERT INTO tugas_selesai (id_account, id_kategori, namaTugas, dueDate, completedDate)" +
                "VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.setInt(2, getidkategori(id, namaKategori));
            stmt.setString(3, namaTugas);
            stmt.setString(4, dueDate);
            stmt.setString(5, String.valueOf(LocalDate.now()));

            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean InsertTugasTidakSelesai(int id, String namaKategori, String namaTugas, String dueDate) {
        String query = "INSERT INTO tugas_tidak_selesai (id_account, id_kategori, namaTugas, dueDate)" +
                "VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.setInt(2, getidkategori(id, namaKategori));
            stmt.setString(3, namaTugas);
            stmt.setString(4, dueDate);

            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public ObservableList<TugasSelesai> ShowCompletedTaskDB(int idacc) {
        String query = "select t.namaTugas, k.namaKategori as kategori, t.dueDate, t.completedDate " +
                "from tugas_selesai t join kategori k on t.id_kategori = k.id_kategori " +
                "where t.id_account=?;";
        ObservableList<TugasSelesai> list = FXCollections.observableArrayList();

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, String.valueOf(idacc));

            ResultSet rs = stmt.executeQuery();
            int cnt = 1;

            while (rs.next()) {

                list.add(
                        new TugasSelesai(
                                String.valueOf(cnt),
                                rs.getString("namaTugas"),
                                rs.getString("kategori"),
                                rs.getString("dueDate"),
                                rs.getString("completedDate")
                        )
                );

                cnt++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } return list;
    }

    public ObservableList<TugasSelesai> ShowUncompletedTaskDB(int idacc) {
        String query = "select t.namaTugas, k.namaKategori as kategori, t.dueDate " +
                "from tugas_tidak_selesai as t join kategori as k on t.id_kategori = k.id_kategori " +
                "where t.id_account=?;";
        ObservableList<TugasSelesai> list = FXCollections.observableArrayList();

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, String.valueOf(idacc));

            ResultSet rs = stmt.executeQuery();
            int cnt = 1;

            while (rs.next()) {

                list.add(
                        new TugasSelesai(
                                String.valueOf(cnt),
                                rs.getString("namaTugas"),
                                rs.getString("kategori"),
                                rs.getString("dueDate")
                        )
                );

                cnt++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } return list;
    }

    public int getJumlahTask(int idacc, String table) {
        String query = "SELECT COUNT(*) from " + table + " where id_account = ?;";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, String.valueOf(idacc));
            ResultSet res = stmt.executeQuery();

            if (res.next()) {return res.getInt(1);}
            else return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public ObservableList<PieChart.Data> getCompletedTaskBasedOnCategory(int idacc) {

        String query = "select k.namaKategori as ktgr, count(*) as jmlh from\n" +
                " tugas_selesai as t join kategori as k on t.id_kategori = k.id_kategori where t.id_account=?" +
                "group by ktgr";

        ObservableList<PieChart.Data> pie = FXCollections.observableArrayList();
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, String.valueOf(idacc));
            ResultSet res = stmt.executeQuery();

            while (res.next()) {
                String k = res.getString("ktgr");
                int j = res.getInt("jmlh");

                pie.add(new PieChart.Data(k, j));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pie;
    }

    public ObservableList<PieChart.Data> getAllTaskBasedOnCategory(int idacc) {
        /* semua Task in a pie chart based on Category */
        String query_completed = "select k.namaKategori as ktgr, count(*) as jmlh from" +
                " tugas_selesai as t join kategori as k on t.id_kategori = k.id_kategori where t.id_account=?" +
                " group by ktgr";

        String query_uncompleted = "select k.namaKategori as ktgr, count(*) as jmlh from" +
                " tugas_tidak_selesai as t join kategori as k on t.id_kategori = k.id_kategori where t.id_account=?" +
                " group by ktgr";

        String query_now = "select k.namaKategori as ktgr, count(*) as jmlh from" +
                " tugas as t join kategori as k on t.id_kategori = k.id_kategori where t.id_account=?" +
                " group by ktgr";

        String query_combined = "SELECT ktgr, SUM(jmlh) as total FROM (" +
                query_completed + " UNION ALL " +
                query_uncompleted + " UNION ALL " +
                query_now + " ) " +
                " GROUP BY ktgr";

        ObservableList<PieChart.Data> pie = FXCollections.observableArrayList();
        try (PreparedStatement stmt = con.prepareStatement(query_combined)) {
            stmt.setString(1, String.valueOf(idacc));
            stmt.setString(2, String.valueOf(idacc));
            stmt.setString(3, String.valueOf(idacc));
            ResultSet res = stmt.executeQuery();

            while (res.next()) {
                String k = res.getString("ktgr");
                int j = res.getInt("total");

                pie.add(new PieChart.Data(k, j));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pie;
    }

//    Semisal Mau Testing DatabaseController :
//    public static void main(String[] args) throws Exception {
//
//        DatabaseController db = new DatabaseController();
//        System.out.println(db.getJumlahCompletedTask(2));
//
//    }

}