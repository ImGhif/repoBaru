package dao;

import config.databaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.BarangKeluar;
import model.DetailKeluar;

public class TransaksiKeluarDAO {
    
    // 1. Ambil Data Pelanggan untuk JComboBox Dropdown
    public List<String[]> getComboPelanggan() {
        List<String[]> list = new ArrayList<>();
        Connection conn = databaseConnection.getConnection();
        String sql = "SELECT id_pelanggan, nama_pel FROM tb_pelanggan";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{rs.getString("id_pelanggan"), rs.getString("nama_pel")});
            }
        } catch (SQLException e) {
            System.err.println("Gagal load combo pelanggan: " + e.getMessage());
        }
        return list;
    }

    // 2. Auto Generate Nomor Faktur Keluar (Contoh: OUT-20260517-001)
    public String generateNoKeluar() {
        Connection conn = databaseConnection.getConnection();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        String today = sdf.format(new java.util.Date());
        
        String sql = "SELECT MAX(CAST(SUBSTRING(no_trans_keluar, 14) AS UNSIGNED)) AS max_id FROM tb_barang_keluar WHERE no_trans_keluar LIKE ?";
        String nextId = "OUT-" + today + "-001";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "OUT-" + today + "-%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int maxId = rs.getInt("max_id");
                    if (maxId > 0) {
                        nextId = String.format("OUT-%s-%03d", today, maxId + 1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal generate No Keluar: " + e.getMessage());
        }
        return nextId;
    }

    // 3. Simpan Transaksi Keluar Lengkap (Induk + Detail)
    public boolean insertTransaksiKeluar(BarangKeluar bk, List<DetailKeluar> listDetail) {
        Connection conn = databaseConnection.getConnection();
        String sqlInduk = "INSERT INTO tb_barang_keluar (no_trans_keluar, id_pelanggan, id_user, tgl_keluar) VALUES (?, ?, ?, ?)";
        String sqlDetail = "INSERT INTO tb_detail_keluar (no_trans_keluar, id_barang, qty_keluar) VALUES (?, ?, ?)";
        
        PreparedStatement psInduk = null;
        PreparedStatement psDetail = null;
        
        try {
            conn.setAutoCommit(false); // Aktifkan ACID Transaction
            
            // A. Insert induk barang keluar
            psInduk = conn.prepareStatement(sqlInduk);
            psInduk.setString(1, bk.getNoTransKeluar());
            psInduk.setString(2, bk.getIdPelanggan());
            psInduk.setString(3, bk.getIdUser());
            psInduk.setDate(4, new java.sql.Date(bk.getTglKeluar().getTime()));
            psInduk.executeUpdate();
            
            // B. Insert detail list barang keluar via batch
            psDetail = conn.prepareStatement(sqlDetail);
            for (DetailKeluar detail : listDetail) {
                psDetail.setString(1, bk.getNoTransKeluar());
                psDetail.setString(2, detail.getIdBarang());
                psDetail.setInt(3, detail.getQtyKeluar());
                psDetail.addBatch();
            }
            psDetail.executeBatch();
            
            conn.commit(); // Eksekusi sukses bersamaan
            return true;
            
        } catch (SQLException e) {
            System.err.println("Transaksi Keluar gagal! Rollback dilakukan. Eror: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Gagal rollback: " + ex.getMessage());
            }
            return false;
        } finally {
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