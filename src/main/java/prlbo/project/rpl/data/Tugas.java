package prlbo.project.rpl.data;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Tugas {
    private String nama;
    private String namakategori;
    private String duedate;
    private BooleanProperty status;

    public Tugas(String nama, String namakategori, String duedate, boolean status) {
        this.nama = nama;
        this.namakategori = namakategori;
        this.duedate = duedate;
        this.status = new SimpleBooleanProperty(status);
    }

    // Getter
    public String getNama() {
        return nama;
    }

    public String getNamakategori() {
        return namakategori;
    }

    public String getDueDate() {
        return duedate;
    }

    public boolean isStatus() {
        return status.get();
    }

    // Setter
    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setNamakategori(String namakategori) {
        this.namakategori = namakategori;
    }

    public void setDueDate(String duedate) {
        this.duedate = duedate;
    }

    public void setStatus(boolean status) {
        this.status.set(status);
    }


    public BooleanProperty statusProperty() {
        return status;
    }
}
