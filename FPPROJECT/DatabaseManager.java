package FPPROJECT;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// import tambahan untuk stats player :)
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {

    private final String dbUrl;

    public DatabaseManager() {
        // constructor URL database dari Config
        this.dbUrl = "jdbc:mysql://" + Config.DB_HOST + ":" + Config.DB_PORT + "/" + Config.DB_NAME + "?sslmode=require";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver tidak ditemukan!");
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, Config.DB_USER, Config.DB_PASSWORD);
    }

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
                    // Bandingkan langsung password dari database sama yg diinput player
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

    public void updateUserStats(String username, boolean isWin) {
        // SQL query disesuaikan dengan nama kolom baru: win, lose, matchPlayed
        String sql = "UPDATE users SET matchPlayed = matchPlayed + 1, " +
                "win = win + ?, lose = lose + ? WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (isWin) {
                stmt.setInt(1, 1); // Tambah 1 ke kolom win
                stmt.setInt(2, 0); // tdk menambah apapun ke lose
            } else {
                stmt.setInt(1, 0); // tdk menambah aapun ke win
                stmt.setInt(2, 1); // Tambah 1 ke kolom lose
            }
            stmt.setString(3, username);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("SQL error selama pembaruan statistik: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // menampilkan stats player
    public Map<String, Integer> getUserStats(String username) {
        String sql = "SELECT win, lose, matchPlayed FROM users WHERE username = ?";
        Map<String, Integer> stats = new HashMap<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // nampilkan data sesuai kolom yg ada di db tictactoe
                    stats.put("win", rs.getInt("win"));
                    stats.put("lose", rs.getInt("lose"));
                    stats.put("matchPlayed", rs.getInt("matchPlayed"));
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL error selama mengambil statistik.");
            e.printStackTrace();
        }
        return stats;
    }

}
