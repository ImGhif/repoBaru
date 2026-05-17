package model;

public class DetailPO {
    private int idDetPo;
    private String noPo;
    private String idBarang;
    private String namaBarang; // Tambahan properti untuk JTable keranjang
    private double hargaBeli;  // Tambahan properti untuk JTable keranjang
    private int qtyPesan;

    public DetailPO() {}

    // Getter dan Setter
    public int getIdDetPo() { return idDetPo; }
    public void setIdDetPo(int idDetPo) { this.idDetPo = idDetPo; }

    public String getNoPo() { return noPo; }
    public void setNoPo(String noPo) { this.noPo = noPo; }

    public String getIdBarang() { return idBarang; }
    public void setIdBarang(String idBarang) { this.idBarang = idBarang; }

    public String getNamaBarang() { return namaBarang; }
    public void setNamaBarang(String namaBarang) { this.namaBarang = namaBarang; }

    public double getHargaBeli() { return hargaBeli; }
    public void setHargaBeli(double hargaBeli) { this.hargaBeli = hargaBeli; }

    public int getQtyPesan() { return qtyPesan; }
    public void setQtyPesan(int qtyPesan) { this.qtyPesan = qtyPesan; }
}