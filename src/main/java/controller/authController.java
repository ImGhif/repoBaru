package controller;

import dao.UserDAO;
import javax.swing.JOptionPane;
import model.User;
import view.loginFrame;
import view.mainFrame;

public class authController {
    
    private final UserDAO userDAO;
    
    public authController() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Logika untuk memproses aksi login dari loginFrame
     * @param frame referensi objek loginFrame yang sedang aktif
     * @param username string dari txtUsername
     * @param password string dari txtPassword
     */
    public void processLogin(loginFrame frame, String username, String password) {
        // 1. Validasi Input Kosong
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                    "Username atau Password tidak boleh kosong!", 
                    "Peringatan", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 2. Cek ke Database via DAO
        User userAktif = userDAO.loginCheck(username, password);
        
        // 3. Evaluasi Hasil Login
        if (userAktif != null) {
            JOptionPane.showMessageDialog(frame, 
                    "Login Berhasil! Selamat Datang, " + userAktif.getNamaUser(), 
                    "Sukses", 
                    JOptionPane.INFORMATION_MESSAGE);
            
            // Buka mainFrame dan kirim data userAktif agar mainFrame tahu hak aksesnya
            mainFrame menuUtama = new mainFrame(userAktif);
            menuUtama.setVisible(true);
            
            // Tutup loginFrame
            frame.dispose();
        } else {
            // Jika login gagal
            JOptionPane.showMessageDialog(frame, 
                    "Username atau Password salah!", 
                    "Login Gagal", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}