
import java.util.Scanner;

import java.sql.*;

/*
 * javac -cp "lib/sqlite-jdbc-3.49.1.0.jar" -d bin file.java
 *  java -cp "bin:lib/sqlite-jdbc-3.49.1.0.jar" file 
 */

public class DatabaseController {
    private Connection con;

    DatabaseController() throws Exception {
        this.con = DriverManager.getConnection("jdbc:sqlite:DMAC.db");
        System.out.println("DB connected");
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

    public static void main(String[] args) throws Exception {

        DatabaseController db = new DatabaseController();
        System.out.println(db.login("ur1", "1"));

    }

}