package controller;

import dao.TransaksiMasukDAO;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.PO;
import model.DetailPO;
import view.transaksimasukPanel;

public class TransaksiController {
    
    private final TransaksiMasukDAO transaksiDAO;
    private final List<DetailPO> keranjangSementara;
    
    public TransaksiController() {
        this.transaksiDAO = new TransaksiMasukDAO();
        this.keranjangSementara = new ArrayList<>();
    }
    
    /**
     * Set nilai awal form saat transaksi baru dibuka
     */
    public void prepareNewTransaction(transaksimasukPanel panel, model.User userAktif) {
        panel.getTxtNoPO().setText(transaksiDAO.generateNoPO());
        panel.getTxtTanggal().setText(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        panel.getTxtTanggal().setEditable(false); // Tanggal transaksi otomatis tidak boleh diedit manual
        panel.getTxtPetugas().setText(userAktif.getNamaUser());
        
        // Kosongkan keranjang sementara
        keranjangSementara.clear();
        refreshTableKeranjang(panel);
        clearInputItem(panel);
        
        // Load data Supplier ke JComboBox
        panel.getCbSupplier().removeAllItems();
        List<String[]> suppliers = transaksiDAO.getComboSupplier();
        for (String[] s : suppliers) {
            panel.getCbSupplier().addItem(s[0] + " - " + s[1]);
        }
    }
    
    /**
     * Membersihkan inputan item barang setelah dimasukkan ke keranjang
     */
    public void clearInputItem(transaksimasukPanel panel) {
        panel.getTxtPilihBarang().setText("");
        panel.getTxtHargaBeli().setText("");
        panel.getTxtQty().setText("");
    }
    
    /**
     * Logika menambahkan item barang ke keranjang sementara (Java List & JTable)
     */
    public void addItemToCart(transaksimasukPanel panel, String idBarang, String namaBarang, double hargaBeli) {
        String qtyStr = panel.getTxtQty().getText().trim();
        
        if (idBarang.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Pilih barang dan isi Qty terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0) {
                JOptionPane.showMessageDialog(panel, "Qty harus lebih dari 0!", "Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean barangSudahAda = false;
            for (DetailPO item : keranjangSementara) {
                if (item.getIdBarang().equals(idBarang)) {
                    item.setQtyPesan(item.getQtyPesan() + qty);
                    barangSudahAda = true;
                    break;
                }
            }
            
            if (!barangSudahAda) {
                DetailPO detail = new DetailPO();
                detail.setIdBarang(idBarang);
                detail.setNamaBarang(namaBarang);
                detail.setHargaBeli(hargaBeli);
                detail.setQtyPesan(qty);
                keranjangSementara.add(detail);
            }
            
            refreshTableKeranjang(panel);
            clearInputItem(panel);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(panel, "Qty harus berupa angka bulat!", "Format Salah", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Menghapus item tertentu dari keranjang sementara berdasarkan baris JTable yang dipilih
     */
    public void removeItemFromCart(transaksimasukPanel panel) {
        int selectedRow = panel.getTblDetailPO().getSelectedRow();
        if (selectedRow != -1) {
            keranjangSementara.remove(selectedRow);
            refreshTableKeranjang(panel);
        } else {
            JOptionPane.showMessageDialog(panel, "Pilih baris item di tabel keranjang yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Sinkronisasi data dari List Java ke komponen JTable UI
     */
    private void refreshTableKeranjang(transaksimasukPanel panel) {
        String[] header = {"No", "Kode Barang", "Nama Barang", "Harga Beli", "Qty", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(null, header) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        panel.getTblDetailPO().setModel(model);
        
        int no = 1;
        for (DetailPO item : keranjangSementara) {
            double subtotal = item.getHargaBeli() * item.getQtyPesan();
            Object[] row = {
                no++,
                item.getIdBarang(),
                item.getNamaBarang(),
                item.getHargaBeli(),
                item.getQtyPesan(),
                subtotal
            };
            model.addRow(row);
        }
    }
    
    /**
     * Eksekusi simpan seluruh data transaksi (Induk + Detail) ke MySQL
     */
    public void saveTransaction(transaksimasukPanel panel, model.User userAktif) {
        if (keranjangSementara.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Keranjang masih kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (panel.getCbSupplier().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(panel, "Pilih supplier terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String selectedSupplier = (String) panel.getCbSupplier().getSelectedItem();
        String idSupplier = selectedSupplier.split(" - ")[0];
        
        PO po = new PO();
        po.setNoPo(panel.getTxtNoPO().getText());
        po.setIdSupplier(idSupplier);
        po.setIdUser(userAktif.getIdUser());
        po.setTglPo(new java.util.Date());
        po.setStatusPo("Selesai");
        
        if (transaksiDAO.insertTransaksi(po, keranjangSementara)) {
            JOptionPane.showMessageDialog(panel, "Transaksi PO Berhasil Disimpan!\nStok otomatis ter-update via Trigger Database.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            prepareNewTransaction(panel, userAktif); 
        } else {
            JOptionPane.showMessageDialog(panel, "Gagal menyimpan transaksi.", "Eror", JOptionPane.ERROR_MESSAGE);
        }
    }
}