package dao;

import config.databaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.PO;
import model.DetailPO;

public class TransaksiMasukDAO {
    
    // 1. Ambil Nama & ID Supplier untuk JComboBox Dropdown
    public List<String[]> getComboSupplier() {
        List<String[]> list = new ArrayList<>();
        Connection conn = databaseConnection.getConnection();
        String sql = "SELECT id_supplier, nama_supl FROM tb_supplier";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{rs.getString("id_supplier"), rs.getString("nama_supl")});
            }
        } catch (SQLException e) {
            System.err.println("Gagal load combo supplier: " + e.getMessage());
        }
        return list;
    }

    // 2. Auto Generate Nomor PO (Contoh: PO-20260517-001)
    public String generateNoPO() {
        Connection conn = databaseConnection.getConnection();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        String today = sdf.format(new java.util.Date());
        
        String sql = "SELECT MAX(CAST(SUBSTRING(no_po, 13) AS UNSIGNED)) AS max_id FROM tb_po WHERE no_po LIKE ?";
        String nextId = "PO-" + today + "-001";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "PO-" + today + "-%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int maxId = rs.getInt("max_id");
                    if (maxId > 0) {
                        nextId = String.format("PO-%s-%03d", today, maxId + 1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal generate No PO: " + e.getMessage());
        }
        return nextId;
    }

    // 3. Simpan Transaksi Lengkap (Induk + Semua Detail Keranjang)
    public boolean insertTransaksi(PO po, List<DetailPO> listDetail) {
        Connection conn = databaseConnection.getConnection();
        String sqlInduk = "INSERT INTO tb_po (no_po, id_supplier, id_user, tgl_po, status_po) VALUES (?, ?, ?, ?, ?)";
        String sqlDetail = "INSERT INTO tb_detail_po (no_po, id_barang, qty_pesan) VALUES (?, ?, ?)";
        
        PreparedStatement psInduk = null;
        PreparedStatement psDetail = null;
        
        try {
            // Matikan auto-commit demi mengaktifkan sistem ACID Transaction
            conn.setAutoCommit(false);
            
            // A. Insert data induk PO
            psInduk = conn.prepareStatement(sqlInduk);
            psInduk.setString(1, po.getNoPo());
            psInduk.setString(2, po.getIdSupplier());
            psInduk.setString(3, po.getIdUser());
            psInduk.setDate(4, new java.sql.Date(po.getTglPo().getTime()));
            psInduk.setString(5, po.getStatusPo());
            psInduk.executeUpdate();
            
            // B. Insert seluruh item di keranjang menggunakan Batch Processing (Lebih cepat)
            psDetail = conn.prepareStatement(sqlDetail);
            for (DetailPO detail : listDetail) {
                psDetail.setString(1, po.getNoPo());
                psDetail.setString(2, detail.getIdBarang());
                psDetail.setInt(3, detail.getQtyPesan());
                psDetail.addBatch();
            }
            psDetail.executeBatch();
            
            // Jika kedua proses di atas tanpa eror, commit ke database permanen
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Transaksi gagal! Rollback dijalankan. Eror: " + e.getMessage());
            try {
                if (conn != null) conn.rollback(); // Batalkan semua jika ada satu saja item eror
            } catch (SQLException ex) {
                System.err.println("Gagal melakukan rollback: " + ex.getMessage());
            }
            return false;
        } finally {
            // Kembalikan status auto commit ke true dan tutup resource
            try {
                conn.setAutoCommit(true);
                if (psInduk != null) psInduk.close();
                if (psDetail != null) psDetail.close();
            } catch (SQLException e) {
                System.err.println("Gagal mereset auto-commit: " + e.getMessage());
            }
        }
    }
}