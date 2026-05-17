package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class databaseConnection {
    
    // Variabel statis untuk menyimpan satu-satunya instance koneksi
    private static Connection connection;
    
    // Konfigurasi parameter database MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/db_gudang";
    private static final String USER = "root";       // Sesuaikan dengan user MySQL kamu
    private static final String PASSWORD = "";       // Sesuaikan dengan password MySQL kamu
    
    // Constructor dibuat private agar kelas ini tidak bisa di-instansiasi dengan kata kunci 'new'
    private databaseConnection() {
    }
    
    /**
     * Method untuk mengambil koneksi ke database (Singleton)
     * @return Connection
     */
    public static Connection getConnection() {
        try {
            // Jika koneksi belum dibuat atau sudah tertutup, buat koneksi baru
            if (connection == null || connection.isClosed()) {
                synchronized (databaseConnection.class) {
                    if (connection == null || connection.isClosed()) {
                        // Me-load driver MySQL (Opsional pada JDBC modern, tapi aman untuk ditambahkan)
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        
                        // Membuka koneksi ke MySQL
                        connection = DriverManager.getConnection(URL, USER, PASSWORD);
                        System.out.println("Koneksi ke Database MySQL Berhasil!");
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL tidak ditemukan: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Gagal terhubung ke database: " + e.getMessage());
        }
        return connection;
    }
    
    /**
     * Method opsional untuk memutus koneksi jika diperlukan
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Koneksi database berhasil diputuskan.");
            }
        } catch (SQLException e) {
            System.err.println("Gagal menutup koneksi database: " + e.getMessage());
        }
    }
}