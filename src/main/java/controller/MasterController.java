package controller;

import dao.BarangDAO;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Barang;
import view.BarangPanel;

public class MasterController {
    
    private final BarangDAO barangDAO;
    
    public MasterController() {
        this.barangDAO = new BarangDAO();
    }
    
    /**
     * Mengisi data dari database ke dalam JTable pada BarangPanel
     */
    public void loadTableData(BarangPanel panel) {
        String[] header = {"Kode Barang", "Nama Barang", "Harga Beli", "Harga Jual", "Stok"};
        DefaultTableModel model = new DefaultTableModel(null, header) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        panel.getTblBarang().setModel(model);
        List<Barang> list = barangDAO.getAllBarang();
        
        for (Barang b : list) {
            Object[] row = {
                b.getIdBarang(),
                b.getNamaBrg(),
                b.getHargaBeli(),
                b.getHargaJual(),
                b.getStok() // Tampilkan juga di halaman master barang utama
            };
            model.addRow(row);
        }
    }
    
    /**
     * Mempersiapkan form untuk input baru (Set ID Otomatis & Bersihkan Field)
     */
    public void clearForm(BarangPanel panel) {
        panel.getTxtKodeBarang().setText(barangDAO.generateAutoId());
        panel.getTxtKodeBarang().setEditable(false); // ID Otomatis tidak boleh diubah manual
        panel.getTxtNamaBarang().setText("");
        panel.getTxtHargaBeli().setText("");
        panel.getTxtHargaJual().setText("");
        
        // Kembalikan status tombol ke kondisi siap input baru
        panel.getBtnSimpan().setEnabled(true);
        panel.getBtnUbah().setEnabled(false);
        panel.getBtnHapus().setEnabled(false);
    }
    
    /**
     * Logika menghitung otomatis Harga Jual (Markup 20%) dari Harga Beli
     */
    public void hitungMarkupHarga(BarangPanel panel) {
        String hargaBeliStr = panel.getTxtHargaBeli().getText().trim();
        if (!hargaBeliStr.isEmpty()) {
            try {
                double hargaBeli = Double.parseDouble(hargaBeliStr);
                double hargaJual = hargaBeli * 1.20; // Otomatis naik 20%
                panel.getTxtHargaJual().setText(String.valueOf(hargaJual));
            } catch (NumberFormatException e) {
                panel.getTxtHargaJual().setText("0");
            }
        } else {
            panel.getTxtHargaJual().setText("");
        }
    }
    
    /**
     * Logika untuk Aksi Simpan Data (Insert)
     */
    public void saveData(BarangPanel panel) {
        String id = panel.getTxtKodeBarang().getText();
        String nama = panel.getTxtNamaBarang().getText().trim();
        String beliStr = panel.getTxtHargaBeli().getText().trim();
        String jualStr = panel.getTxtHargaJual().getText().trim();
        
        if (nama.isEmpty() || beliStr.isEmpty() || jualStr.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Semua kolom input wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            double hargaBeli = Double.parseDouble(beliStr);
            double hargaJual = Double.parseDouble(jualStr);
            
            // PERBAIKAN: Menambahkan parameter default stok (0) agar cocok dengan constructor baru Barang
            Barang b = new Barang(id, nama, hargaBeli, hargaJual, 0);
            if (barangDAO.insertBarang(b)) {
                JOptionPane.showMessageDialog(panel, "Data barang berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadTableData(panel);
                clearForm(panel);
            } else {
                JOptionPane.showMessageDialog(panel, "Gagal menyimpan data barang.", "Eror", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(panel, "Input Harga harus berupa angka!", "Format Salah", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Logika untuk Aksi Ubah Data (Update)
     */
    public void updateData(BarangPanel panel) {
        String id = panel.getTxtKodeBarang().getText();
        String nama = panel.getTxtNamaBarang().getText().trim();
        String beliStr = panel.getTxtHargaBeli().getText().trim();
        String jualStr = panel.getTxtHargaJual().getText().trim();
        
        if (nama.isEmpty() || beliStr.isEmpty() || jualStr.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Semua kolom input wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            double hargaBeli = Double.parseDouble(beliStr);
            double hargaJual = Double.parseDouble(jualStr);
            
            // PERBAIKAN: Menambahkan parameter stok (0) agar tidak error compile.
            // Sifat update hanya mengubah nama dan harga, untuk jumlah stok dikendalikan mutasi PO/Keluar.
            Barang b = new Barang(id, nama, hargaBeli, hargaJual, 0);
            if (barangDAO.updateBarang(b)) {
                JOptionPane.showMessageDialog(panel, "Data barang berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadTableData(panel);
                clearForm(panel);
            } else {
                JOptionPane.showMessageDialog(panel, "Gagal diperbarui.", "Eror", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(panel, "Input Harga harus berupa angka!", "Format Salah", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Logika untuk Aksi Hapus Data (Delete)
     */
    public void deleteData(BarangPanel panel) {
        String id = panel.getTxtKodeBarang().getText();
        int konfirmasi = JOptionPane.showConfirmDialog(panel, "Yakin ingin menghapus barang " + id + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        
        if (konfirmasi == JOptionPane.YES_OPTION) {
            if (barangDAO.deleteBarang(id)) {
                JOptionPane.showMessageDialog(panel, "Data berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadTableData(panel);
                clearForm(panel);
            } else {
                JOptionPane.showMessageDialog(panel, "Gagal hapus data. Barang terikat transaksi PO/Keluar.", "Eror", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}