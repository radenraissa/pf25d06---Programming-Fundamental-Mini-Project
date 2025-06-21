package FPPROJECT;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * Tic-Tac-Toe: Two-player Graphic version with better OO design.
 * The Board and Cell classes are separated in their own classes.
 */
public class GameMain extends JPanel {
    private static final long serialVersionUID = 1L; // to prevent serializable warning

    // Define named constants for the drawing graphics
    public static final String TITLE = "Tic Tac Toe";
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_CROSS = new Color(239, 105, 80);  // Red #EF6950
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225); // Blue #409AE1
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    // Define game objects
    private Board board;         // the game board
    private State currentState;  // the current state of the game
    private Seed currentPlayer;  // the current player
    private JLabel statusBar;    // for displaying status message

    private GameMode currentMode; // sebagai penyimpan mode saat ini
    private BotPlayer bot; // menambahkan instance setelah class BotPlayer

    /** Constructor to setup the UI and game components */
    // constructor diubah untuk menerima mode game yang dipilih
    public GameMain(GameMode mode) {
        this.currentMode = mode;
        if (this.currentMode == GameMode.LOCAL_PVE){
            bot = new BotPlayer();
        } // jika memilih PVE, program akan menginisiasi pemain BOT untuk melawan player

        // This JPanel fires MouseEvent
        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {  // mouse-clicked handler

            if (currentState == State.PLAYING) {
                if (currentMode == GameMode.LOCAL_PVE && currentPlayer == Seed.NOUGHT){
                    return; // jangan beri input apa-apa, abaikan player jika giliran BOT
                }

                int mouseX = e.getX();
                int mouseY = e.getY();
                // Get the row and column clicked
                int row = mouseY / Cell.SIZE;
                int col = mouseX / Cell.SIZE;



                if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                        && board.cells[row][col].content == Seed.NO_SEED) {
                    // Update cells[][] and return the new game state after the move
                    currentState = board.stepGame(currentPlayer, row, col);
                    // Switch player
                    currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;

                    // JIKA mode PvE dan sekarang giliran bot (NOUGHT), panggil bot
                    if (currentMode == GameMode.LOCAL_PVE && currentPlayer == Seed.NOUGHT && currentState == State.PLAYING) {
                        // Beri jeda agar gerakan bot tidak terasa instan
                        Timer timer = new Timer(200, ae -> {
                            botMove(); // bot memilih kolom
                            repaint(); // gambar ulang setelah bot memilih kolom
                        });
                        timer.setRepeats(false);
                        timer.start();
                    }
                }
            } else {        // game over
                newGame();  // restart the game
            }
            // Refresh the drawing canvas
            repaint();  // Callback paintComponent().
        }
        });

        // Setup the status bar (JLabel) to display status message
        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        super.setLayout(new BorderLayout());
        super.add(statusBar, BorderLayout.PAGE_END); // same as SOUTH
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));
        // account for statusBar in height
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        // Set up Game
        initGame();
        newGame();
    }

    // mengambil koordinat array pergerakan bot dari 1D array move
    private void botMove() {
        if (currentState == State.PLAYING && currentPlayer == Seed.NOUGHT) {
            int[] move = bot.makeMove(board);
            int row = move[0];
            int col = move[1];
            currentState = board.stepGame(currentPlayer, row, col);
            currentPlayer = Seed.CROSS;
        }
    }

    /** Initialize the game (run once) */
    public void initGame() {
        board = new Board();  // allocate the game-board
    }

    /** Reset the game-board contents and the current-state, ready for new game */
    public void newGame() {
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = Seed.NO_SEED; // all cells empty
            }
        }
        currentPlayer = Seed.CROSS;    // cross plays first
        currentState = State.PLAYING;  // ready to play
    }

    /** Custom painting codes on this JPanel */
    @Override
    public void paintComponent(Graphics g) {  // Callback via repaint()
        super.paintComponent(g);
        setBackground(COLOR_BG); // set its background color

        board.paint(g);  // ask the game board to paint itself

        // Print status-bar message
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

    /** The entry "main" method */
    public static void main(String[] args) {
        // Run GUI construction codes in Event-Dispatching thread for thread safety
        javax.swing.SwingUtilities.invokeLater(() -> {

            //menampilkan pilihan mode
            Object[] options = {"LOCAL PVP", "PVE (vs bot)"};
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Pilih Mode: ",
                    "Tic Tac Toe",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

            // menangani exit program
            if (choice == JOptionPane.CLOSED_OPTION) {
                System.exit(0);
            }

            // menyimpan mode yang dipilih player
            GameMode selectedMode = (choice == 0) ? GameMode.LOCAL_PVP : GameMode.LOCAL_PVE;

            // buat frame dan panel mode permainan yang dipilih player
            JFrame frame = new JFrame(TITLE);
            // Set the content-pane of the JFrame to an instance of main JPanel

            frame.setContentPane(new GameMain(selectedMode));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null); // center the application window
            frame.setVisible(true);            // show it

        });
    }
}
