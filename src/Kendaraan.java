
public class Kendaraan {
    private String platNomor;
    private String jenis;
    private String waktuMasuk;
    private int harga;
    private String waktuKeluar;

    public Kendaraan(String platNomor, String waktuMasuk, int harga, String jenis) {
        this.jenis = jenis;
        this.platNomor = platNomor;
        this.waktuMasuk = waktuMasuk;
        this.harga = harga;
    }

    public String getJenis() {
        return jenis;
    }

    public String getPlatNomor() {
        return platNomor;
    }

    public String getWaktuMasuk() {
        return waktuMasuk;
    }

    public int getHarga() {
        return harga;
    }

    public String getWaktuKeluar() {
        return waktuKeluar;
    }

    public void setWaktuKeluar(String waktuKeluar) {
        this.waktuKeluar = waktuKeluar;
    }
}
