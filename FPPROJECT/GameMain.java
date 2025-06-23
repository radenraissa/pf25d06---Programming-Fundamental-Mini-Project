package FPPROJECT;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.Map;

public class GameMain extends JPanel {
    private static final long serialVersionUID = 1L;

    private JButton viewStatsButton;
    private JButton quitButton; // Add this line

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

    public GameMain(GameMode mode, String username){

        this.dbManager = new DatabaseManager();
        this.loggedInUser = username;

        this.currentMode = mode;
        if (this.currentMode == GameMode.LOCAL_PVE){
            bot = new BotPlayer();
        }

        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentState == State.PLAYING) {
                    if (currentMode == GameMode.LOCAL_PVE && currentPlayer == Seed.NOUGHT){
                        return;
                    }

                    int mouseX = e.getX();
                    int mouseY = e.getY();
                    int row = mouseY / Cell.SIZE;
                    int col = mouseX / Cell.SIZE;

                    if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                            && board.cells[row][col].content == Seed.NO_SEED) {

                        SoundManager.playSound(currentPlayer.getSoundFilename()); // pen-trigger suara
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

        // 2. Setup Tombol lihat stats
        viewStatsButton = new JButton("Lihat Statistik");

        // NEW: Setup Quit Button
        quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to quit?",
                    "Confirm Quit",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0); // Exits the application
            }
        });

        // 3. Buat Panel untuk bagian bawah yang menampung statusBar dan tombol
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(statusBar, BorderLayout.CENTER);

        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Align buttons to the right
        buttonContainer.add(viewStatsButton);
        buttonContainer.add(quitButton); // Add the quit button

        southPanel.add(buttonContainer, BorderLayout.EAST); // Add the button container to the EAST


        // 4. Tambahkan Action Listener ke Tombol
        viewStatsButton.addActionListener(e -> {
            // Hanya jalankan jika pengguna sudah login (misal, tidak dalam mode offline tanpa login)
            if (loggedInUser == null || loggedInUser.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Anda harus login untuk melihat statistik.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

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
        });


        // 5. Setup Layout Utama dan Tambahkan Panel
        super.setLayout(new BorderLayout());
        super.add(southPanel, BorderLayout.PAGE_END); // Tambahkan panel bawah yang baru
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        // Set up Game
        initGame();
        newGame();
    }

    private void botMove() {
        SoundManager.playSound(currentPlayer.getSoundFilename()); // pen-trigger suara gerakan bot
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
        // Jangan lakukan apa-apa saat TTT masih dimainkan
        if (currentState == State.PLAYING) {
            return;
        }

        // akan meng-update statistik selain seri
        if ((currentState == State.CROSS_WON || currentState == State.NOUGHT_WON) && currentMode == GameMode.LOCAL_PVE) {
            boolean playerWon = (currentState == State.CROSS_WON);
            dbManager.updateUserStats(loggedInUser, playerWon);
            System.out.println("Statistik untuk " + loggedInUser + " telah diperbarui.");
        }

//        if (currentState == State.DRAW && currentMode == GameMode.LOCAL_PVE) {
//        }

        // restart game setelah diperbarui statistikny
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

    /**
     * The entry "main" method.
     * The application now starts with the LoginFrame.
     */
    public static void main(String[] args) {
        // Run GUI construction codes in Event-Dispatching thread for thread safety
        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
