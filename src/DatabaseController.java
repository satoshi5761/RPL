
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
        System.out.println("database connected");
    }

    public boolean register(String username, String passwd) {
        String query = "INSERT INTO account (username, password) VALUES (?, ?);";
        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setString(1, username);
            stmt.setString(2, passwd);
            
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Presumably, UNIQUE entry condition has been breached");
            /* Note: NEED TO HANDLE THIS ON JAVAFX GUI POP UP */
            return false;
        }
    }

    public boolean login(String username, String passwd) throws SQLException{
        String query = "SELECT (username, password) FROM account WHERE username=? AND password=?;";

        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1, username);
        stmt.setString(2, passwd);

        ResultSet res = stmt.executeQuery();
        return res.next();

    }

    public static void main(String[] args) throws Exception {

        DatabaseController db = new DatabaseController();
        System.out.println(db.login("ur1", "1"));

    }

}