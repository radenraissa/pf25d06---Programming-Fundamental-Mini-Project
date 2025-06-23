package FPPROJECT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

public class GameMain extends JPanel {
    private static final long serialVersionUID = 1L;

    // --- Deklarasi Tombol Baru ---
    private JButton viewStatsButton;
    private JButton backToLoginButton; // Tombol baru untuk kembali ke login

    public static final String TITLE = "Tic Tac Toe";
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_CROSS = new Color(239, 105, 80);
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225);
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    private Board board;
    private State currentState;
    private Seed currentPlayer;
    private JLabel statusBar;
    private GameMode currentMode;
    private BotPlayer bot;

    private DatabaseManager dbManager;
    private String loggedInUser;

    public GameMain(GameMode mode, String username) {

        this.dbManager = new DatabaseManager();
        this.loggedInUser = username;

        this.currentMode = mode;
        if (this.currentMode == GameMode.LOCAL_PVE) {
            bot = new BotPlayer();
        }

        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentState == State.PLAYING) {
                    if (currentMode == GameMode.LOCAL_PVE && currentPlayer == Seed.NOUGHT) {
                        return;
                    }

                    int mouseX = e.getX();
                    int mouseY = e.getY();
                    int row = mouseY / Cell.SIZE;
                    int col = mouseX / Cell.SIZE;

                    if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                            && board.cells[row][col].content == Seed.NO_SEED) {


                        SoundManager.playSound(currentPlayer.getSoundFilename());
                        currentState = board.stepGame(currentPlayer, row, col);
                        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;

                        if (currentMode == GameMode.LOCAL_PVE && currentPlayer == Seed.NOUGHT && currentState == State.PLAYING) {
                            Timer timer = new Timer(200, ae -> {
                                botMove();
                                repaint();
                            });
                            timer.setRepeats(false);
                            timer.start();
                        }
                    }
                } else {
                    handleEndOfGame();
                }
                repaint();
            }
        });

        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        // Inisialisasi tombol-tombol
        viewStatsButton = new JButton("Lihat Statistik");
        backToLoginButton = new JButton("Logout"); // Inisialisasi tombol baru

        // Buat Panel untuk bagian bawah yang menampung statusBar dan tombol
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(statusBar, BorderLayout.CENTER);
        southPanel.add(viewStatsButton, BorderLayout.EAST);
        southPanel.add(backToLoginButton, BorderLayout.WEST); // Tambahkan tombol baru ke panel

        // Tambahkan Action Listener untuk tombol Lihat Statistik
        viewStatsButton.addActionListener(e -> showStats());

        // --- Tambahkan Action Listener untuk Tombol Kembali ke Login ---
        backToLoginButton.addActionListener(e -> {
            // Dapatkan frame (jendela) tempat panel game ini berada
            Window gameFrame = SwingUtilities.getWindowAncestor(this);
            if (gameFrame != null) {
                gameFrame.dispose(); // Tutup jendela game saat ini
            }

            // Buka kembali jendela login
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });

        // Setup Layout Utama dan Tambahkan Panel
        super.setLayout(new BorderLayout());
        super.add(southPanel, BorderLayout.PAGE_END);
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        // Set up Game
        initGame();
        newGame();
    }

    private void showStats() {
        Map<String, Integer> stats = dbManager.getUserStats(loggedInUser);
        if (stats != null && !stats.isEmpty()) {
            String message = String.format(
                    "Statistik untuk: %s\n\nMatches Played: %d\nWins: %d\nLosses: %d",
                    loggedInUser,
                    stats.get("matchPlayed"),
                    stats.get("win"),
                    stats.get("lose")
            );
            JOptionPane.showMessageDialog(this, message, "Statistik Pemain", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Tidak dapat mengambil statistik untuk pengguna: " + loggedInUser, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void botMove() {

        SoundManager.playSound(currentPlayer.getSoundFilename());
        if (currentState == State.PLAYING && currentPlayer == Seed.NOUGHT) {
            int[] move = bot.makeMove(board);
            int row = move[0];
            int col = move[1];
            currentState = board.stepGame(currentPlayer, row, col);
            currentPlayer = Seed.CROSS;
        }
    }

    public void initGame() {
        board = new Board();
    }

    public void newGame() {
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = Seed.NO_SEED;
            }
        }
        currentPlayer = Seed.CROSS;
        currentState = State.PLAYING;
    }

    private void handleEndOfGame() {
        if (currentState == State.PLAYING) {
            return;
        }
        if ((currentState == State.CROSS_WON || currentState == State.NOUGHT_WON) && currentMode == GameMode.LOCAL_PVE) {
            boolean playerWon = (currentState == State.CROSS_WON);
            dbManager.updateUserStats(loggedInUser, playerWon);
            System.out.println("Statistik untuk " + loggedInUser + " telah diperbarui.");
        }
        newGame();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(COLOR_BG);
        board.paint(g);

        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            statusBar.setText((currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn");
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'X' Won! Click to play again.");
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'O' Won! Click to play again.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}