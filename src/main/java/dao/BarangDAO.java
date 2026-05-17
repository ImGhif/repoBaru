package dao;

import config.databaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Barang;

public class BarangDAO {
    
    // 1. Ambil Semua Data Barang beserta nilai stok ter-update
    public List<Barang> getAllBarang() {
        List<Barang> list = new ArrayList<>();
        Connection conn = databaseConnection.getConnection();
        // Query membaca kolom stok dari tb_barang
        String sql = "SELECT id_barang, nama_brg, harga_beli, harga_jual, stok FROM tb_barang ORDER BY id_barang DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Barang b = new Barang();
                b.setIdBarang(rs.getString("id_barang"));
                b.setNamaBrg(rs.getString("nama_brg"));
                b.setHargaBeli(rs.getDouble("harga_beli"));
                b.setHargaJual(rs.getDouble("harga_jual"));
                b.setStok(rs.getInt("stok")); // Set nilai langsung ke objek Barang
                list.add(b);
            }
        } catch (SQLException e) {
            System.err.println("Gagal ambil data barang beserta stok: " + e.getMessage());
        }
        return list;
    }

    // 2. Tambah Barang Baru (Insert)
    public boolean insertBarang(Barang barang) {
        Connection conn = databaseConnection.getConnection();
        // Saat insert pertama kali, kolom stok di MySQL idealnya memiliki default value 0
        String sql = "INSERT INTO tb_barang (id_barang, nama_brg, harga_beli, harga_jual) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, barang.getIdBarang());
            ps.setString(2, barang.getNamaBrg());
            ps.setDouble(3, barang.getHargaBeli());
            ps.setDouble(4, barang.getHargaJual());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal insert barang: " + e.getMessage());
            return false;
        }
    }

    // 3. Ubah Data Barang (Update)
    public boolean updateBarang(Barang barang) {
        Connection conn = databaseConnection.getConnection();
        String sql = "UPDATE tb_barang SET nama_brg = ?, harga_beli = ?, harga_jual = ? WHERE id_barang = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, barang.getNamaBrg());
            ps.setDouble(2, barang.getHargaBeli());
            ps.setDouble(3, barang.getHargaJual());
            ps.setString(4, barang.getIdBarang());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal update barang: " + e.getMessage());
            return false;
        }
    }

    // 4. Hapus Barang (Delete)
    public boolean deleteBarang(String idBarang) {
        Connection conn = databaseConnection.getConnection();
        String sql = "DELETE FROM tb_barang WHERE id_barang = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idBarang);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal delete barang: " + e.getMessage());
            return false;
        }
    }

    // 5. Auto Generate Kode Barang
    public String generateAutoId() {
        Connection conn = databaseConnection.getConnection();
        String sql = "SELECT MAX(CAST(SUBSTRING(id_barang, 4) AS UNSIGNED)) AS max_id FROM tb_barang";
        String nextId = "BRG001";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                if (maxId > 0) {
                    nextId = String.format("BRG%03d", maxId + 1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal generate ID barang: " + e.getMessage());
        }
        return nextId;
    }
}