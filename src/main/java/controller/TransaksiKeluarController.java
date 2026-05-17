package controller;

import dao.TransaksiKeluarDAO;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.BarangKeluar;
import model.DetailKeluar;
import view.transaksikeluarPanel;

public class TransaksiKeluarController {
    
    private final TransaksiKeluarDAO transaksiKeluarDAO;
    private final List<DetailKeluar> keranjangKeluar;
    
    public TransaksiKeluarController() {
        this.transaksiKeluarDAO = new TransaksiKeluarDAO();
        this.keranjangKeluar = new ArrayList<>();
    }
    
    public void prepareNewTransaction(transaksikeluarPanel panel, model.User userAktif) {
        panel.getTxtNoTransaksi().setText(transaksiKeluarDAO.generateNoKeluar());
        panel.getTxtTanggal().setText(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        panel.getTxtTanggal().setEditable(false);
        panel.getTxtPetugas().setText(userAktif.getNamaUser());
        panel.getTxtPelanggan().setText(""); // Bersihkan input nama pelanggan
        
        keranjangKeluar.clear();
        refreshTableKeranjang(panel);
        clearInputItem(panel);
    }
    
    public void clearInputItem(transaksikeluarPanel panel) {
        panel.getTxtPilihBarang().setText("");
        panel.getTxtHargaJual().setText("");
        panel.getTxtQty().setText("");
    }
    
    public void addItemToCart(transaksikeluarPanel panel, String idBarang, String namaBarang, double hargaJual, int stokTersedia) {
        String qtyStr = panel.getTxtQty().getText().trim();
        
        if (idBarang.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Pilih barang dan isi Qty terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0) {
                JOptionPane.showMessageDialog(panel, "Qty keluar harus lebih dari 0!", "Eror", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Periksa total qty yang diinput + yang sudah ada di keranjang (jika ada)
            int qtyDiKeranjang = 0;
            for (DetailKeluar item : keranjangKeluar) {
                if (item.getIdBarang().equals(idBarang)) {
                    qtyDiKeranjang = item.getQtyKeluar();
                    break;
                }
            }
            
            // VALIDASI UTAMA: Tolak jika (Qty Input + Qty di Keranjang) melebihi Stok Gudang
            if ((qty + qtyDiKeranjang) > stokTersedia) {
                JOptionPane.showMessageDialog(panel, 
                        "Stok tidak mencukupi!\nSisa stok di gudang saat ini: " + stokTersedia + " item.", 
                        "Transaksi Ditolak", 
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Logika tumpuk barang di keranjang jika lolos validasi
            boolean barangSudahAda = false;
            for (DetailKeluar item : keranjangKeluar) {
                if (item.getIdBarang().equals(idBarang)) {
                    item.setQtyKeluar(item.getQtyKeluar() + qty);
                    barangSudahAda = true;
                    break;
                }
            }
            
            if (!barangSudahAda) {
                DetailKeluar detail = new DetailKeluar();
                detail.setIdBarang(idBarang);
                detail.setNamaBarang(namaBarang);
                detail.setHargaJual(hargaJual);
                detail.setQtyKeluar(qty);
                keranjangKeluar.add(detail);
            }
            
            refreshTableKeranjang(panel);
            clearInputItem(panel);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(panel, "Qty harus berupa angka bulat!", "Format Salah", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void removeItemFromCart(transaksikeluarPanel panel) {
        int selectedRow = panel.getTblKeranjang().getSelectedRow();
        if (selectedRow != -1) {
            keranjangKeluar.remove(selectedRow);
            refreshTableKeranjang(panel);
        } else {
            JOptionPane.showMessageDialog(panel, "Pilih item keranjang yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * PERBAIKAN UTAMA: Header kolom tabel keranjang disesuaikan agar informatif 
     * menampilkan Qty Keluar beserta Subtotal kalkulasinya.
     */
    private void refreshTableKeranjang(transaksikeluarPanel panel) {
        String[] header = {"No", "Kode Barang", "Nama Barang", "Harga Jual", "Qty Keluar", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(null, header) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        panel.getTblKeranjang().setModel(model);
        
        int no = 1;
        for (DetailKeluar item : keranjangKeluar) {
            double subtotal = item.getHargaJual() * item.getQtyKeluar();
            Object[] row = {
                no++,
                item.getIdBarang(),
                item.getNamaBarang(),
                item.getHargaJual(),
                item.getQtyKeluar(), // Menampilkan kuantitas yang ditarik dari gudang
                subtotal
            };
            model.addRow(row);
        }
    }
    
public void saveTransaction(transaksikeluarPanel panel, model.User userAktif) {
        String namaPelanggan = panel.getTxtPelanggan().getText().trim();
        
        if (namaPelanggan.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Nama Pelanggan wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (keranjangKeluar.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Keranjang keluar masih kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        BarangKeluar bk = new BarangKeluar();
        bk.setNoTransKeluar(panel.getTxtNoTransaksi().getText());
        
        // PERBAIKAN UTAMA: Masukkan ID Pelanggan yang VALID dan ada di tb_pelanggan
        // Sesuaikan 'PLG001' dengan ID Pelanggan Umum yang kamu insert di database tadi
        bk.setIdPelanggan("PLG001"); 
        
        bk.setIdUser(userAktif.getIdUser());
        bk.setTglKeluar(new java.util.Date());
        
        if (transaksiKeluarDAO.insertTransaksiKeluar(bk, keranjangKeluar)) {
            JOptionPane.showMessageDialog(panel, 
                    "Transaksi Keluar Sukses!\nStok otomatis berkurang via Trigger Database MySQL.", 
                    "Sukses", 
                    JOptionPane.INFORMATION_MESSAGE);
            prepareNewTransaction(panel, userAktif); // Refresh form & update sisa stok
        } else {
            JOptionPane.showMessageDialog(panel, "Gagal memproses pengeluaran barang. Cek konsol eror.", "Eror", JOptionPane.ERROR_MESSAGE);
        }
    }
}