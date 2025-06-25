package FPPROJECT;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Objects;

public class FrameManager extends JFrame {
    private final DatabaseManager dbManager;

    private JTextField usernameField;
    private JPasswordField passwordField;

    public FrameManager() {
        super(GameMain.TITLE + " - Login");
        this.dbManager = new DatabaseManager();
        setupFuturisticUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void setupFuturisticUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(GameMain.COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(GameMain.FONT_STATUS);
        userLabel.setForeground(GameMain.COLOR_TEXT);
        gc.gridx = 0;
        gc.gridy = 0;
        mainPanel.add(userLabel, gc);

        usernameField = createFuturisticTextField();
        gc.gridx = 1;
        gc.gridy = 0;
        mainPanel.add(usernameField, gc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(GameMain.FONT_STATUS);
        passLabel.setForeground(GameMain.COLOR_TEXT);
        gc.gridx = 0;
        gc.gridy = 1;
        mainPanel.add(passLabel, gc);

        passwordField = createFuturisticPasswordField();
        gc.gridx = 1;
        gc.gridy = 1;
        mainPanel.add(passwordField, gc);

        JButton loginButton = createFuturisticButton("Login");
        JButton registerButton = createFuturisticButton("Register");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(GameMain.COLOR_BG);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gc.gridx = 0;
        gc.gridy = 2;
        gc.gridwidth = 2;
        gc.insets = new Insets(15, 5, 5, 5);
        mainPanel.add(buttonPanel, gc);

        loginButton.addActionListener(this::performLogin);
        registerButton.addActionListener(this::performRegister);
    }



    private JTextField createFuturisticTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(GameMain.FONT_STATUS);
        textField.setBackground(GameMain.COLOR_BUTTON_BG);
        textField.setForeground(GameMain.COLOR_TEXT);
        textField.setCaretColor(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GameMain.COLOR_GRID),
                new EmptyBorder(5, 5, 5, 5)
        ));
        return textField;
    }

    private JPasswordField createFuturisticPasswordField() {
        JPasswordField pf = new JPasswordField(20);
        pf.setFont(GameMain.FONT_STATUS);
        pf.setBackground(GameMain.COLOR_BUTTON_BG);
        pf.setForeground(GameMain.COLOR_TEXT);
        pf.setCaretColor(Color.WHITE);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GameMain.COLOR_GRID),
                new EmptyBorder(5, 5, 5, 5)
        ));
        return pf;
    }

    private JButton createFuturisticButton(String text) {
        JButton button = new JButton(text);
        button.setFont(GameMain.FONT_BUTTON);
        button.setForeground(GameMain.COLOR_BUTTON_TEXT);
        button.setBackground(GameMain.COLOR_BUTTON_BG);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GameMain.COLOR_GRID, 1),
                new EmptyBorder(8, 25, 8, 25)
        ));
        button.setFocusPainted(false);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(GameMain.COLOR_BUTTON_HOVER);
                button.setForeground(Color.BLACK);
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(GameMain.COLOR_BUTTON_BG);
                button.setForeground(GameMain.COLOR_BUTTON_TEXT);
            }
        });
        return button;
    }

    private void styleOptionPane() {
        UIManager.put("OptionPane.background", GameMain.COLOR_BG);
        UIManager.put("Panel.background", GameMain.COLOR_BG);
        UIManager.put("OptionPane.messageForeground", GameMain.COLOR_TEXT);
        UIManager.put("Button.background", GameMain.COLOR_BUTTON_BG);
        UIManager.put("Button.foreground", GameMain.COLOR_BUTTON_TEXT);
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
    }

    private void performLogin(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        styleOptionPane();
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

        styleOptionPane();
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
        Seed.CROSS.resetToDefault();
        Seed.NOUGHT.resetToDefault();

        javax.swing.SwingUtilities.invokeLater(() -> {
            styleOptionPane();
            Object[] options = {"LOCAL PVP", "PVE (vs bot)"};
            int choice = JOptionPane.showOptionDialog(
                    null, "Pilih Mode:", "Tic Tac Toe",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

            if (choice == JOptionPane.CLOSED_OPTION) {
                System.exit(0);
            }
            GameMode selectedMode = (choice == 0) ? GameMode.LOCAL_PVP : GameMode.LOCAL_PVE;
            showCharacterSelectionAndStartGame(selectedMode, username);
        });
    }

    private void showCharacterSelectionAndStartGame(GameMode selectedMode, String username) {

        Map<String, String> characterImages = Map.of(
                "Default X", "FPPROJECT/images/x/xIcon.png",
                "Boneca Labu", "FPPROJECT/images/x/boneca_labu.jpg",
                "Bombardiro Croc", "FPPROJECT/images/x/Bombardiro_crocodilo.jpg",
                "Cappuccino Assasin", "FPPROJECT/images/x/cappuccino_assassino.jpg",
                "Default O", "FPPROJECT/images/o/oIcon.png",
                "Tung Tung", "FPPROJECT/images/o/tung_tung.jpg",
                "Tripi Tropi", "FPPROJECT/images/o/tripitropi.jpg",
                "Trallalero Trala", "FPPROJECT/images/o/Trallalero_Trallala.jpg"
        );

        // 2. Map atau alamat untuk path suara
        Map<String, String> characterSounds = Map.of(
                // suara utk X
                "Default X", "FPPROJECT/audio/pencilsfx.wav",
                "Boneca Labu", "FPPROJECT/audio/x/ambalabu-cut.wav",
                "Bombardiro Croc", "FPPROJECT/audio/x/bombardiro-cut.wav",
                "Cappuccino Assasin", "FPPROJECT/audio/x/cappuccino-cut.wav",

                // suara utk O
                "Default O", "FPPROJECT/audio/pencilsfx.wav",
                "Trallalero Trala", "FPPROJECT/audio/o/tralalero-cut.wav",
                "Tung Tung", "FPPROJECT/audio/o/tungtungtung-cut.wav",
                "Tripi Tropi", "FPPROJECT/audio/o/tripitropi-cut.wav"

        );

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 5));
        panel.setBackground(GameMain.COLOR_BG);

        JComboBox<String> player1Box = new JComboBox<>(characterImages.keySet().toArray(new String[0]));
        JComboBox<String> player2Box = new JComboBox<>(characterImages.keySet().toArray(new String[0]));
        player2Box.setSelectedIndex(4);

        styleComboBox(player1Box);
        styleComboBox(player2Box);

        JLabel p1Label = new JLabel("Pemain 1 (X):");
        p1Label.setForeground(GameMain.COLOR_TEXT);
        p1Label.setFont(GameMain.FONT_STATUS);
        JLabel p2Label = new JLabel("Pemain 2 (O):");
        p2Label.setForeground(GameMain.COLOR_TEXT);
        p2Label.setFont(GameMain.FONT_STATUS);

        panel.add(p1Label);
        panel.add(player1Box);
        panel.add(p2Label);
        panel.add(player2Box);

        styleOptionPane();
        int result = JOptionPane.showConfirmDialog(
                null, panel, "Pilih Karakter",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String p1SelectionName = (String) player1Box.getSelectedItem();
            String p2SelectionName = (String) player2Box.getSelectedItem();

            if (Objects.equals(p1SelectionName, p2SelectionName)) {
                JOptionPane.showMessageDialog(null, "Pemain tidak boleh menggunakan karakter yang sama.", "Error", JOptionPane.ERROR_MESSAGE);
                showCharacterSelectionAndStartGame(selectedMode, username);
                return;
            }

            Seed.CROSS.setImageAndSound(characterImages.get(p1SelectionName), characterSounds.get(p1SelectionName));
            Seed.NOUGHT.setImageAndSound(characterImages.get(p2SelectionName), characterSounds.get(p2SelectionName));

            startGameFrame(selectedMode, username);
        } else {
            System.exit(0);
        }
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setBackground(GameMain.COLOR_BUTTON_BG);
        comboBox.setForeground(GameMain.COLOR_BUTTON_TEXT);
        comboBox.setFont(GameMain.FONT_BUTTON);
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
