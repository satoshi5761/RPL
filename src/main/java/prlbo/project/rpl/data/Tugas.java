package prlbo.project.rpl.data;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Tugas {
    private int id;
    private int idacc;
    private String nama;
    private String namakategori;
    private String duedate;
    private BooleanProperty status;

    public Tugas(int id, int idacc, String nama, String namakategori, String duedate, boolean status) {
        this.id = id;
        this.idacc = idacc;
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

    public int getIdacc() {
        return idacc;
    }

    public void setIdacc(int idacc) {
        this.idacc = idacc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDuedate() {
        return duedate;
    }

    public void setDuedate(String duedate) {
        this.duedate = duedate;
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
