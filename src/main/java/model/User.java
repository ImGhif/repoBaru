package model;

public class User {
    private String idUser;
    private int idRole;
    private String namaUser;
    private String username;
    private String password;
    private String namaRole; // Tambahan properti untuk mempermudah deteksi role di UI

    // Constructor Kosong
    public User() {}

    // Constructor Lengkap
    public User(String idUser, int idRole, String namaUser, String username, String password) {
        this.idUser = idUser;
        this.idRole = idRole;
        this.namaUser = namaUser;
        this.username = username;
        this.password = password;
    }

    // Getter dan Setter
    public String getIdUser() { return idUser; }
    public void setIdUser(String idUser) { this.idUser = idUser; }

    public int getIdRole() { return idRole; }
    public void setIdRole(int idRole) { this.idRole = idRole; }

    public String getNamaUser() { return namaUser; }
    public void setNamaUser(String namaUser) { this.namaUser = namaUser; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNamaRole() { return namaRole; }
    public void setNamaRole(String namaRole) { this.namaRole = namaRole; }
}