package prlbo.project.rpl.Controller;

import java.sql.*;

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

    //Semisal Mau Testing DatabaseController :
//    public static void main(String[] args) throws Exception {
//
//        DatabaseController db = new DatabaseController();
//        System.out.println(db.login("ur1", "1"));
//
//    }

}