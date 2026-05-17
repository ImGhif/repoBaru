package model;

import java.util.Date;

public class PO {
    private String noPo;
    private String idSupplier;
    private String idUser;
    private Date tglPo;
    private String statusPo;

    public PO() {}

    public PO(String noPo, String idSupplier, String idUser, Date tglPo, String statusPo) {
        this.noPo = noPo;
        this.idSupplier = idSupplier;
        this.idUser = idUser;
        this.tglPo = tglPo;
        this.statusPo = statusPo;
    }

    // Getter dan Setter
    public String getNoPo() { return noPo; }
    public void setNoPo(String noPo) { this.noPo = noPo; }

    public String getIdSupplier() { return idSupplier; }
    public void setIdSupplier(String idSupplier) { this.idSupplier = idSupplier; }

    public String getIdUser() { return idUser; }
    public void setIdUser(String idUser) { this.idUser = idUser; }

    public Date getTglPo() { return tglPo; }
    public void setTglPo(Date tglPo) { this.tglPo = tglPo; }

    public String getStatusPo() { return statusPo; }
    public void setStatusPo(String statusPo) { this.statusPo = statusPo; }
}