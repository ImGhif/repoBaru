package model;

public class DetailKeluar {
    private int idDetKeluar;
    private String noTransKeluar;
    private String idBarang;
    private String namaBarang; // Untuk JTable keranjang
    private double hargaJual;  // Menggunakan harga jual, bukan harga beli
    private int qtyKeluar;

    public DetailKeluar() {}

    // Getter dan Setter
    public int getIdDetKeluar() { return idDetKeluar; }
    public void setIdDetKeluar(int idDetKeluar) { this.idDetKeluar = idDetKeluar; }

    public String getNoTransKeluar() { return noTransKeluar; }
    public void setNoTransKeluar(String noTransKeluar) { this.noTransKeluar = noTransKeluar; }

    public String getIdBarang() { return idBarang; }
    public void setIdBarang(String idBarang) { this.idBarang = idBarang; }

    public String getNamaBarang() { return namaBarang; }
    public void setNamaBarang(String namaBarang) { this.namaBarang = namaBarang; }

    public double getHargaJual() { return hargaJual; }
    public void setHargaJual(double hargaJual) { this.hargaJual = hargaJual; }

    public int getQtyKeluar() { return qtyKeluar; }
    public void setQtyKeluar(int qtyKeluar) { this.qtyKeluar = qtyKeluar; }
}