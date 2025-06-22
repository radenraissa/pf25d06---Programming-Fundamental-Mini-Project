package FPPROJECT;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {

    private final String dbUrl;

    public DatabaseManager() {
        // Konstruksi URL database dari kelas Config
        this.dbUrl = "jdbc:mysql://" + Config.DB_HOST + ":" + Config.DB_PORT + "/" + Config.DB_NAME + "?sslmode=require";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver tidak ditemukan!");
            e.printStackTrace();
        }
    }

    /**
     * Membuat koneksi ke database.
     * @return Objek Connection.
     * @throws SQLException jika terjadi error akses database.
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, Config.DB_USER, Config.DB_PASSWORD);
    }

    /**
     * Mencoba mendaftarkan pengguna baru ke database.
     * @param username Username yang diinginkan.
     * @param password Password plain-text yang diinginkan.
     * @return true jika registrasi berhasil, false jika username sudah ada atau terjadi error.
     */
    public boolean registerUser(String username, String password) {
        // SQL untuk memasukkan pengguna baru dengan password plain-text
        String insertSql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {

            stmt.setString(1, username);
            stmt.setString(2, password); // Simpan password langsung
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            // Error ini bisa terjadi jika username sudah ada (karena ada batasan UNIQUE)
            // atau karena masalah koneksi lainnya.
            System.err.println("SQL error selama registrasi: " + e.getMessage());
            // e.printStackTrace(); // Anda bisa uncomment ini untuk debug lebih lanjut
            return false;
        }
    }

    /**
     * Mencoba login pengguna dengan memverifikasi kredensial.
     * @param username Username pengguna.
     * @param password Password pengguna.
     * @return true jika username dan password cocok, false jika tidak.
     */
    public boolean loginUser(String username, String password) {
        // SQL untuk mengambil password yang tersimpan
        String sql = "SELECT password FROM users WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Ambil password dari database
                    String storedPassword = rs.getString("password");
                    // Bandingkan langsung password dari database dengan yang diinput pengguna
                    return storedPassword.equals(password);
                } else {
                    return false; // Pengguna tidak ditemukan
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL error selama login.");
            e.printStackTrace();
            return false;
        }
    }
}
