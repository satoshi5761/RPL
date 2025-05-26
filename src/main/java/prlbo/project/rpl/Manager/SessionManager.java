package prlbo.project.rpl.Manager;

import java.io.*;
import java.time.LocalDate;
import java.util.Date;

public class SessionManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String SESSION_FILE = "session.ser";
    private static volatile SessionManager instance;
    private boolean isLoggedIn;
    private LocalDate date;
    private String username, password;

    // Private constructor to prevent instantiation from outside
    private SessionManager() {
        isLoggedIn = false;
    }

    // Static method to get the singleton instance
    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                    instance.createSessionFile();
                }
            }
        }
        return instance;
    }

    private void createSessionFile() {
        File file = new File(SESSION_FILE);
        if (!file.exists()) {
            saveSession();
        } else {
            loadSession();
        }

    }

    private void loadSession() {
        try (ObjectInputStream ois = new ObjectInputStream(new
                FileInputStream(SESSION_FILE))) {
            SessionManager sessionManager = (SessionManager)
                    ois.readObject();
            this.isLoggedIn = sessionManager.isLoggedIn;
            this.date = sessionManager.date;
            username = sessionManager.username;
            password = sessionManager.password;
            if (date == null) {
                isLoggedIn = false;
            } else {
                int selisihHari = LocalDate.now().getDayOfYear() - date.getDayOfYear();
                if (selisihHari > 3 || selisihHari < 0) {
                    isLoggedIn = false;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading session: " +
                    e.getMessage());
        }
    }

    private void saveSession() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new
                FileOutputStream(SESSION_FILE))) {
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Method to check if user is logged in
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    // Method to simulate login
    public void login() {
        Date date = new Date();
        LocalDate myObj = LocalDate.now();
        this.date = myObj;
        isLoggedIn = true;
        saveSession();
    }

    // Method to simulate logout
    public void logout() {
        isLoggedIn = false;
        saveSession();
    }

}
