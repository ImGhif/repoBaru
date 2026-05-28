package gudang;

import config.databaseConnection;
import java.sql.Connection;
import view.loginFrame; // Import loginFrame dari package view

public class main {
    public static void main(String[] args) {
        System.out.println("Memulai aplikasi...");
        
        // Memanggil method getConnection untuk tes koneksi
        Connection conn = databaseConnection.getConnection();
        
        if (conn != null) {
            System.out.println("Pondasi Database AMAN! Membuka halaman login...");
            
            // Gunakan java.awt.EventQueue agar GUI Swing berjalan dengan aman di Thread utamanya
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    // Membuat objek loginFrame
                    loginFrame formLogin = new loginFrame();
                    // Menampilkan loginFrame ke layar
                    formLogin.setVisible(true);
                    }
            });
            
        } else {
            System.err.println("Pondasi Database EROR! Aplikasi tidak dapat dijalankan.");
        }
    }
}