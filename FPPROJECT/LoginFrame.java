package FPPROJECT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.Objects;

public class LoginFrame extends JFrame {
    private final DatabaseManager dbManager;

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        super(GameMain.TITLE + " - Login");
        this.dbManager = new DatabaseManager();

        // field uname & pasword
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        // Layout
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0;
        gc.gridy = 0;
        add(new JLabel("Username:"), gc);

        gc.gridx = 1;
        add(usernameField, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        add(new JLabel("Password:"), gc);

        gc.gridx = 1;
        add(passwordField, gc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gc.gridx = 0;
        gc.gridy = 2;
        gc.gridwidth = 2;
        add(buttonPanel, gc);

        // Action Listeners
        loginButton.addActionListener(this::performLogin);
        registerButton.addActionListener(this::performRegister);

        // Frame Setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null); // Center the window
    }

    private void performLogin(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (dbManager.loginUser(username, password)) {
            JOptionPane.showMessageDialog(this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            launchGame(username);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performRegister(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.length() < 3 || password.length() < 4) {
            JOptionPane.showMessageDialog(this, "Username must be at least 3 characters and password at least 4 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (dbManager.registerUser(username, password)) {
            JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Username is already taken.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void launchGame(String username) {
        dispose();

        // Reset gambar ke default setiap kali memulai game baru
        Seed.CROSS.resetToDefaultImage();
        Seed.NOUGHT.resetToDefaultImage();

        // pemilihan mode
        javax.swing.SwingUtilities.invokeLater(() -> {
            Object[] options = {"LOCAL PVP", "PVE (vs bot)"};
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Pilih Mode: ",
                    "Tic Tac Toe",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

            if (choice == JOptionPane.CLOSED_OPTION) {
                System.exit(0);
            }

            GameMode selectedMode = (choice == 0) ? GameMode.LOCAL_PVP : GameMode.LOCAL_PVE;

            showCharacterSelectionAndStartGame(selectedMode, username);
        });
    }

    private void showCharacterSelectionAndStartGame(GameMode selectedMode, String username) {
        // Daftar karakter yang tersedia. Tambahkan karakter baru di sini.
        // Key = Nama Karakter, Value = Path ke Gambar
        Map<String, String> characters = Map.of(
                // x
                "default \"X\"", "FPPROJECT/images/x/xIcon.png",
                "Boneca Labu", "FPPROJECT/images/x/boneca_labu.jpg",
                "Bombardiro Croc", "FPPROJECT/images/x/Bombardiro_crocodilo.jpg",
                "Cappuccino Assasin", "FPPROJECT/images/x/cappuccino_assassino.jpg",
                // o
                "default \"O\"", "FPPROJECT/images/o/oIcon.png",
                "Tung Tung", "FPPROJECT/images/o/tung_tung.jpg",
                "Tripi Tropi", "FPPROJECT/images/o/tripitropi.jpg",
                "Trallalero Trala", "FPPROJECT/images/o/Trallalero_Trallala.jpg"
        );

        // Buat komponen UI untuk dialog
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 5));
        JComboBox<String> player1Box = new JComboBox<>(characters.keySet().toArray(new String[0]));
        JComboBox<String> player2Box = new JComboBox<>(characters.keySet().toArray(new String[0]));
        player2Box.setSelectedIndex(1); // Set pilihan default yang berbeda

        panel.add(new JLabel("Pemain 1 (X):"));
        panel.add(player1Box);
        panel.add(new JLabel("Pemain 2 (O):"));
        panel.add(player2Box);

        // Tampilkan dialog pemilihan
        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Pilih Karakter",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String p1Selection = (String) player1Box.getSelectedItem();
            String p2Selection = (String) player2Box.getSelectedItem();

            if (Objects.equals(p1Selection, p2Selection)) {
                JOptionPane.showMessageDialog(null, "Pemain tidak boleh menggunakan karakter yang sama.", "Error", JOptionPane.ERROR_MESSAGE);
                showCharacterSelectionAndStartGame(selectedMode, username); // Ulangi pemilihan
                return;
            }

            // Atur gambar berdasarkan pilihan
            Seed.CROSS.setImage(characters.get(p1Selection));
            Seed.NOUGHT.setImage(characters.get(p2Selection));

            // Mulai game dengan karakter yang dipilih
            startGameFrame(selectedMode, username);
        } else {
            System.exit(0); // Keluar jika pengguna membatalkan
        }
    }

    private void startGameFrame(GameMode selectedMode, String username) {
        JFrame frame = new JFrame(GameMain.TITLE);
        frame.setContentPane(new GameMain(selectedMode, username));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

