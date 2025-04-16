
import java.util.Scanner;

import java.sql.*;

/*
 * javac -cp "lib/sqlite-jdbc-3.49.1.0.jar" -d bin file.java
 *  java -cp "bin:lib/sqlite-jdbc-3.49.1.0.jar" file 
 */

public class DatabaseController {
    private static Connection con;

    public static void main(String[] args) throws Exception {

        Connection con = DriverManager.getConnection("jdbc:sqlite:DMAC.db");
        System.out.println("database connected");

    }

}