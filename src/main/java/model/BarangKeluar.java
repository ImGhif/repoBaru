package model;

import java.util.Date;

public class BarangKeluar {
    private String noTransKeluar;
    private String idPelanggan;
    private String idUser;
    private Date tglKeluar;

    public BarangKeluar() {}

    public BarangKeluar(String noTransKeluar, String idPelanggan, String idUser, Date tglKeluar) {
        this.noTransKeluar = noTransKeluar;
        this.idPelanggan = idPelanggan;
        this.idUser = idUser;
        this.tglKeluar = tglKeluar;
    }

    // Getter dan Setter
    public String getNoTransKeluar() { return noTransKeluar; }
    public void setNoTransKeluar(String noTransKeluar) { this.noTransKeluar = noTransKeluar; }

    public String getIdPelanggan() { return idPelanggan; }
    public void setIdPelanggan(String idPelanggan) { this.idPelanggan = idPelanggan; }

    public String getIdUser() { return idUser; }
    public void setIdUser(String idUser) { this.idUser = idUser; }

    public Date getTglKeluar() { return tglKeluar; }
    public void setTglKeluar(Date tglKeluar) { this.tglKeluar = tglKeluar; }
}