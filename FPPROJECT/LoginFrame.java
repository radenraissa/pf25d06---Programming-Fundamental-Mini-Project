package FPPROJECT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private final DatabaseManager dbManager;

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        super(GameMain.TITLE + " - Login");
        this.dbManager = new DatabaseManager();

        // UI Components
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
            launchGame();
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

    private void launchGame() {
        // Close the login window
        dispose();

        // Run the game selection and launch logic from GameMain's original main method
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

            JFrame frame = new JFrame(GameMain.TITLE);
            frame.setContentPane(new GameMain(selectedMode));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
