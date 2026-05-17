package model;

public class Barang {
    private String idBarang;
    private String namaBrg;
    private double hargaBeli;
    private double hargaJual;
    private int stok; // Tambahkan properti ini di model Barang

    public Barang() {}

    public Barang(String idBarang, String namaBrg, double hargaBeli, double hargaJual, int stok) {
        this.idBarang = idBarang;
        this.namaBrg = namaBrg;
        this.hargaBeli = hargaBeli;
        this.hargaJual = hargaJual;
        this.stok = stok;
    }

    // Getter dan Setter
    public String getIdBarang() { return idBarang; }
    public void setIdBarang(String idBarang) { this.idBarang = idBarang; }

    public String getNamaBrg() { return namaBrg; }
    public void setNamaBrg(String namaBrg) { this.namaBrg = namaBrg; }

    public double getHargaBeli() { return hargaBeli; }
    public void setHargaBeli(double hargaBeli) { this.hargaBeli = hargaBeli; }

    public double getHargaJual() { return hargaJual; }
    public void setHargaJual(double hargaJual) { this.hargaJual = hargaJual; }

    public int getStok() { return stok; } // Getter stok
    public void setStok(int stok) { this.stok = stok; } // Setter stok
}