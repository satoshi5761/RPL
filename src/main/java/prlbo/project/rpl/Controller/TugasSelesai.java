package prlbo.project.rpl.Controller;

import javafx.beans.property.SimpleStringProperty;

public class TugasSelesai {
    private SimpleStringProperty no;
    private SimpleStringProperty namaTugas;
    private SimpleStringProperty kategori;
    private SimpleStringProperty dueDate;
    private SimpleStringProperty completedDate;

    public TugasSelesai(String no, String namaTugas, String kategori, String dueDate, String completedDate) {
        this.no = new SimpleStringProperty(no);
        this.namaTugas = new SimpleStringProperty(namaTugas);
        this.kategori = new SimpleStringProperty(kategori);
        this.dueDate = new SimpleStringProperty(dueDate);
        this.completedDate = new SimpleStringProperty(completedDate);
    }
    public TugasSelesai(String no, String namaTugas, String kategori, String dueDate) {
        this.no = new SimpleStringProperty(no);
        this.namaTugas = new SimpleStringProperty(namaTugas);
        this.kategori = new SimpleStringProperty(kategori);
        this.dueDate = new SimpleStringProperty(dueDate);
    }

    public String getNo() { return no.get(); }
    public String getNamaTugas() { return namaTugas.get(); }
    public String getKategori() { return kategori.get(); }
    public String getDueDate() { return dueDate.get(); }
    public String getCompletedDate() { return completedDate.get(); }
}
