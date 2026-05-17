package model;

public class Supplier {
    private String idSupplier;
    private String namaSupl;
    private String telpSupl;
    private String alamatSupl;

    // Constructor Kosong
    public Supplier() {}

    // Constructor Lengkap
    public Supplier(String idSupplier, String namaSupl, String telpSupl, String alamatSupl) {
        this.idSupplier = idSupplier;
        this.namaSupl = namaSupl;
        this.telpSupl = telpSupl;
        this.alamatSupl = alamatSupl;
    }

    // Getter dan Setter
    public String getIdSupplier() { return idSupplier; }
    public void setIdSupplier(String idSupplier) { this.idSupplier = idSupplier; }

    public String getNamaSupl() { return namaSupl; }
    public void setNamaSupl(String namaSupl) { this.namaSupl = namaSupl; }

    public String getTelpSupl() { return telpSupl; }
    public void setTelpSupl(String telpSupl) { this.telpSupl = telpSupl; }

    public String getAlamatSupl() { return alamatSupl; }
    public void setAlamatSupl(String alamatSupl) { this.alamatSupl = alamatSupl; }
}