package prlbo.project.rpl.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class DatabaseController {
    private Connection con;

    DatabaseController() throws Exception {
        this.con = DriverManager.getConnection("jdbc:sqlite:DMAC.db");
        System.out.println("DB connected");
    }
    public boolean userValid(String username){
        boolean valid = false;
        String query = "SELECT * FROM account WHERE username = ?";
        try {
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            valid = rs.next();
            stmt.close();
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return valid;
    }

    public boolean forgot(String username, String password) {
        if(!userValid(username)){
            return false;
        }
        String query = "UPDATE account SET password=? WHERE username=?";
        try {
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, password);
            stmt.setString(2, username);
            stmt.executeUpdate();
            System.out.println("Ubah Password berhasil");
            return true;
        } catch (SQLException e) {
            System.out.println("Gagal Chuaks");
            return false;
        }
    }

    public boolean register(String username, String passwd) {
        String query = "INSERT INTO account (username, password) VALUES (?, ?);";
        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setString(1, username);
            stmt.setString(2, passwd);
            stmt.executeUpdate();
            System.out.println("Registration successfull");
            return true;
        } catch (SQLException e) {
            System.out.println("Presumably, UNIQUE entry condition has been breached");
            /* Note: NEED TO HANDLE THIS ON JAVAFX GUI POP UP */
            return false;
        }
    }

    public boolean login(String username, String passwd) throws SQLException{
        String query = "SELECT COUNT(*) FROM account WHERE username=? AND password=?;";

        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1, username);
        stmt.setString(2, passwd);

        ResultSet res = stmt.executeQuery();
        return res.getInt(1) >= 1;

    }

    public void tutup_cinta() {
        /* cintaku bukan di database uhuk uhuk */
        try {
            if (!con.isClosed()) {
                con.close();
                System.out.println("DB closed\n");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public ObservableList<String> loadcomboboxkat(){
        ObservableList<String> kategori = FXCollections.observableArrayList();
        String query = "SELECT namaKategori FROM kategori";
        try {
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                kategori.add(rs.getString("namaKategori"));
            }
        }
        catch (Exception e) {
            System.out.println("Gagal Ambil Kategori.");
        }
        return kategori;
    }

    public boolean TambahTugas(String nama, LocalDate tanggal, String waktu, String kategori) {
        String query = "INSERT INTO daftartugas (namaTugas, tanggalTenggat, waktuTenggat, kategori) VALUES (?, ?, ?, ?);";
        String query2 = "SELECT id_account, id_kategori FROM account natural join kategori WHEREa";


        try {
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, nama);
            stmt.setDate(2, Date.valueOf(tanggal));
            stmt.setString(3, waktu);
            stmt.setString(4, kategori);

            stmt.executeUpdate();
            System.out.println("Tugas berhasil ditambahkan.");
            return true;
        } catch (SQLException e) {
            System.out.println("Tugas gagal ditambahkan.");
            return false;
        }

    }

//    public boolean EditTugas(String nama, LocalDate tanggal, String waktu, String kategori) {
//        String query = "INSERT INTO daftartugas (namaTugas, tanggalTenggat, waktuTenggat, kategori) VALUES (?, ?, ?, ?);";
//
//        try {
//            PreparedStatement stmt = con.prepareStatement(query);
//            stmt.setString(1, nama);
//            stmt.setDate(2, Date.valueOf(tanggal));
//            stmt.setString(3, waktu);
//            stmt.setString(4, kategori);
//
//            stmt.executeUpdate();
//            System.out.println("Tugas berhasil ditambahkan.");
//            return true;
//        } catch (SQLException e) {
//            System.out.println("Tugas gagal ditambahkan.");
//            return false;
//        }
//
//    }
//
//    public boolean HapusTugas(String nama) {
//        String query = "DELETE FROM daftartugas WHERE nama = ?;";
//
//        try {
//            PreparedStatement stmt = con.prepareStatement(query);
//            stmt.setString(1, nama);
//
//            stmt.executeUpdate();
//            System.out.println("Tugas berhasil dihapus.");
//            return true;
//        } catch (SQLException e) {
//            System.out.println("Tugas gagal dihapus.");
//            return false;
//        }
//    }

    //Semisal Mau Testing DatabaseController :
//    public static void main(String[] args) throws Exception {
//
//        DatabaseController db = new DatabaseController();
//        System.out.println(db.login("ur1", "1"));
//
//    }

}