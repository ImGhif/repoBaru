package dao;

import config.databaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.User;

public class UserDAO {
    
    /**
     * Method untuk memvalidasi login user berdasarkan username & password
     * @param username input dari field login
     * @param password input dari field login
     * @return Objek User jika ditemukan, null jika tidak ditemukan
     */
    public User loginCheck(String username, String password) {
        Connection conn = databaseConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = null;
        
        // Query SQL dengan JOIN ke tb_role untuk mendapatkan nama_role secara instan
        String sql = "SELECT u.*, r.nama_role FROM tb_user u " +
                     "JOIN tb_role r ON u.id_role = r.id_role " +
                     "WHERE u.username = ? AND u.password = ?";
        
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password); // Pada tahap lanjut, password ini idealnya menggunakan hashing (seperti MD5/BCrypt)
            
            rs = ps.executeQuery();
            
            // Jika data ditemukan di database
            if (rs.next()) {
                user = new User();
                user.setIdUser(rs.getString("id_user"));
                user.setIdRole(rs.getInt("id_role"));
                user.setNamaUser(rs.getString("nama_user"));
                user.setUsername(rs.getString("username"));
                user.setNamaRole(rs.getString("nama_role")); // Menyimpan role (Owner/Admin/Petugas)
            }
        } catch (SQLException e) {
            System.err.println("Eror saat query login_check: " + e.getMessage());
        } finally {
            // Menutup resource JDBC agar tidak terjadi kebocoran memori (memory leak)
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.err.println("Gagal menutup resource: " + e.getMessage());
            }
        }
        
        return user; // Mengembalikan objek user hasil login
    }
}