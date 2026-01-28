package view;

import model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Swing-based GUI renderer for Tetris.
 * Provides a graphical user interface using Java Swing.
 *
 * Features:
 * - Real-time game board rendering with colored blocks
 * - Statistics panel showing score, lines, difficulty
 * - Non-blocking keyboard input
 * - Responsive graphics at 50Hz
 * - Pause indicator
 */
public class SwingRenderer extends JFrame implements GameView, KeyListener {
    private final GamePanel gamePanel;
    private final StatsPanel statsPanel;
    private final BlockingQueue<GameAction> inputQueue;
    private final Set<Integer> keysPressed;

    private static final int CELL_SIZE = 30;
    private static final int BOARD_WIDTH = Board.COLS * CELL_SIZE;
    private static final int BOARD_HEIGHT = Board.ROWS * CELL_SIZE;

    /**
     * Constructs a new SwingRenderer with Swing UI.
     */
    public SwingRenderer() {
        this.inputQueue = new LinkedBlockingQueue<>();
        this.keysPressed = new HashSet<>();

        setTitle("Tetris - Java Clone");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setBackground(new Color(30, 30, 30));

        // Create main panel with game and stats side by side
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));

        // Left: Game board
        this.gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(BOARD_WIDTH + 20, BOARD_HEIGHT + 20));
        mainPanel.add(gamePanel, BorderLayout.CENTER);

        // Right: Statistics
        this.statsPanel = new StatsPanel();
        statsPanel.setPreferredSize(new Dimension(200, BOARD_HEIGHT + 20));
        mainPanel.add(statsPanel, BorderLayout.EAST);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Add key listener and ensure focus
        addKeyListener(this);
        setFocusable(true);
        requestFocus();
    }

    @Override
    public void render(GameState state) {
        gamePanel.setGameState(state);
        gamePanel.setGameOver(false); // Reset Game Over flag when rendering new game state
        statsPanel.setGameState(state);
        SwingUtilities.invokeLater(() -> {
            gamePanel.repaint();
            statsPanel.repaint();
        });
    }

    @Override
    public GameAction readInput() {
        // First check for queued single-press actions (like DROP, QUIT)
        GameAction queued = inputQueue.poll();
        if (queued != null && (queued == GameAction.DROP || queued == GameAction.PAUSE || queued == GameAction.QUIT)) {
            return queued;
        }
        
        // Otherwise, check for continuous movement actions from held keys
        GameAction currentAction = determineActionFromPressedKeys();
        return currentAction != GameAction.NONE ? currentAction : queued;
    }
    
    /**
     * Determines the action based on currently pressed keys.
     * Prioritizes movement and rotation actions.
     */
    private synchronized GameAction determineActionFromPressedKeys() {
        // Check in order of priority: rotation > horizontal movement > vertical movement
        if (keysPressed.contains(KeyEvent.VK_W) || keysPressed.contains(KeyEvent.VK_UP) || keysPressed.contains(KeyEvent.VK_R)) {
            return GameAction.ROTATE;
        }
        if (keysPressed.contains(KeyEvent.VK_A) || keysPressed.contains(KeyEvent.VK_LEFT)) {
            return GameAction.LEFT;
        }
        if (keysPressed.contains(KeyEvent.VK_D) || keysPressed.contains(KeyEvent.VK_RIGHT)) {
            return GameAction.RIGHT;
        }
        if (keysPressed.contains(KeyEvent.VK_S) || keysPressed.contains(KeyEvent.VK_DOWN)) {
            return GameAction.DOWN;
        }
        return GameAction.NONE;
    }

    @Override
    public void close() {
        SwingUtilities.invokeLater(this::dispose);
    }

    @Override
    public void renderGameOver(GameState state) {
        // Game over is rendered in the game panel
        gamePanel.setGameOver(true);
    }

    @Override
    public void renderHighScores(List<HighScoreManager.HighScore> topScores) {
        StringBuilder sb = new StringBuilder("High Scores:\n\n");
        int rank = 1;
        for (HighScoreManager.HighScore score : topScores) {
            sb.append(String.format("%2d. %6d (%s, L:%d)\n",
                rank++, score.getScore(), score.getDifficulty(), score.getLines()));
            if (rank > 10) break;
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "High Scores", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void renderStatistics(GameStatistics statistics) {
        String stats = String.format(
            "Final Score:    %d\n" +
            "Lines Cleared:  %d\n" +
            "Pieces Placed:  %d\n" +
            "Duration:       %.1fs\n" +
            "Difficulty:     %s\n" +
            "Score/Line:     %.1f\n" +
            "Score/Piece:    %.1f",
            statistics.getFinalScore(),
            statistics.getTotalLinesCleared(),
            statistics.getPiecesPlaced(),
            statistics.getPlayDurationSeconds(),
            statistics.getDifficulty().name(),
            statistics.getScorePerLine(),
            statistics.getScorePerPiece()
        );
        JOptionPane.showMessageDialog(this, stats, "Game Statistics", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void renderHelp() {
        String help = "TETRIS - Controls:\n\n" +
            "A / LEFT      - Move left\n" +
            "D / RIGHT     - Move right\n" +
            "S / DOWN      - Accelerate\n" +
            "W / UP        - Rotate (wall kick)\n" +
            "SPACE / DROP  - Instant drop\n" +
            "P / PAUSE     - Pause/Resume\n" +
            "Q / ESC       - Quit\n\n" +
            "□ - Empty cell\n" +
            "■ - Fixed blocks\n" +
            "◆ - Falling piece";
        JOptionPane.showMessageDialog(this, help, "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void clearScreen() {
        // Not applicable for Swing
    }

    /**
     * Shows the main menu as a dialog in GUI mode.
     *
     * @return selected menu option (1-4)
     */
    @Override
    public int showMainMenu() {
        // Options in correct order for vertical display
        String[] options = {
            "1. Play Game",
            "2. View High Scores",
            "3. Watch Demo",
            "4. Exit"
        };

        int choice = showVerticalOptionDialog(
                "Welcome to Tetris!\n\nWhat would you like to do?",
                "Tetris - Main Menu",
                options,
                0); // Default to Play Game (index 0)

        // Return the choice directly (1-based)
        return choice + 1;
    }

    /**
     * Helper method to show a dialog with buttons in vertical layout.
     */
    private int showVerticalOptionDialog(String message, String title, String[] options, int defaultIndex) {
        JDialog dialog = new JDialog((Frame) null, title, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Add message
        JLabel messageLabel = new JLabel("<html>" + message.replace("\n", "<br>") + "</html>");
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(messageLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Add buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        final int[] choice = {-1};
        
        for (int i = 0; i < options.length; i++) {
            final int index = i;
            JButton button = new JButton(options[i]);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(300, 40));
            button.setPreferredSize(new Dimension(300, 40));
            
            if (i == defaultIndex) {
                button.setFocusPainted(true);
                button.requestFocus();
            }
            
            button.addActionListener(e -> {
                choice[0] = index;
                dialog.dispose();
            });
            
            buttonsPanel.add(button);
            if (i < options.length - 1) {
                buttonsPanel.add(Box.createVerticalStrut(10));
            }
        }
        
        contentPanel.add(buttonsPanel);
        dialog.setContentPane(contentPanel);
        dialog.setSize(400, 300);
        dialog.setVisible(true);
        
        return choice[0] >= 0 ? choice[0] : defaultIndex;
    }

    /**
     * Shows difficulty selection as a dialog in GUI mode.
     * Buttons displayed vertically in reverse order.
     *
     * @return selected GameDifficulty
     */
    @Override
    public GameDifficulty selectDifficulty() {
        // Options in reverse order for vertical display
        String[] options = {
            "EXTREME (fastest, 2.0x bonus)",
            "HARD (faster, 1.5x bonus)",
            "NORMAL (balanced gameplay)",
            "EASY (slowest, no multiplier)"
        };

        int choice = showVerticalOptionDialog(
                "Select Game Difficulty:",
                "Tetris - Difficulty Selection",
                options,
                2); // Default to NORMAL (index 2 in reversed array)

        // Convert choice to difficulty (reverse mapping)
        switch (choice) {
            case 0:
                return GameDifficulty.EXTREME;
            case 1:
                return GameDifficulty.HARD;
            case 2:
                return GameDifficulty.NORMAL;
            case 3:
                return GameDifficulty.EASY;
            default:
                return GameDifficulty.NORMAL;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public synchronized void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        keysPressed.add(keyCode);
        
        // For single-press actions, queue them immediately
        GameAction action = null;
        switch (keyCode) {
            case KeyEvent.VK_SPACE:
                action = GameAction.DROP;
                break;
            case KeyEvent.VK_P:
                action = GameAction.PAUSE;
                break;
            case KeyEvent.VK_Q:
            case KeyEvent.VK_ESCAPE:
                action = GameAction.QUIT;
                break;
        }

        if (action != null) {
            try {
                inputQueue.put(action);
            } catch (InterruptedException ignored) {
            }
        }
    }
    
    /**
     * Tracks when keys are released to update the pressed keys set.
     */
    public synchronized void keyReleased(KeyEvent e) {
        keysPressed.remove(e.getKeyCode());
    }

    /**
     * Panel for rendering the game board.
     */
    private class GamePanel extends JPanel {
        private GameState gameState;
        private boolean gameOver = false;

        private static final Color COLOR_EMPTY = new Color(50, 50, 50);
        private static final Color COLOR_FILLED = new Color(100, 150, 255);
        private static final Color COLOR_FALLING = new Color(255, 200, 0);
        private static final Color COLOR_BORDER = new Color(150, 150, 150);

        GamePanel() {
            setBackground(COLOR_EMPTY);
            setFocusable(true);
            addKeyListener(new java.awt.event.KeyListener() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {
                    SwingRenderer.this.keyPressed(e);
                }

                @Override
                public void keyTyped(java.awt.event.KeyEvent e) {
                }

                @Override
                public void keyReleased(java.awt.event.KeyEvent e) {
                }
            });
        }

        void setGameState(GameState state) {
            this.gameState = state;
        }

        void setGameOver(boolean gameOver) {
            this.gameOver = gameOver;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (gameState == null) {
                return;
            }

            Board board = gameState.getBoard();
            Tetromino piece = gameState.getCurrentPiece();

            // Draw border
            g2d.setColor(COLOR_BORDER);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(5, 5, BOARD_WIDTH + 10, BOARD_HEIGHT + 10);

            // Draw board cells
            for (int row = 0; row < Board.ROWS; row++) {
                for (int col = 0; col < Board.COLS; col++) {
                    int x = 10 + col * CELL_SIZE;
                    int y = 10 + row * CELL_SIZE;

                    model.Point cell = new model.Point(row, col);
                    if (piece.getOccupiedCells().contains(cell)) {
                        g2d.setColor(COLOR_FALLING);
                        g2d.fillRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
                    } else if (board.isOccupied(cell)) {
                        g2d.setColor(COLOR_FILLED);
                        g2d.fillRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
                    }

                    // Grid lines
                    g2d.setColor(COLOR_BORDER);
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                }
            }

            // Draw game over overlay
            if (gameOver) {
                g2d.setColor(new Color(0, 0, 0, 200));
                g2d.fillRect(5, 5, BOARD_WIDTH + 10, BOARD_HEIGHT + 10);
                g2d.setColor(Color.RED);
                g2d.setFont(new Font("Arial", Font.BOLD, 40));
                FontMetrics fm = g2d.getFontMetrics();
                String text = "GAME OVER";
                int x = (BOARD_WIDTH - fm.stringWidth(text)) / 2 + 10;
                int y = BOARD_HEIGHT / 2;
                g2d.drawString(text, x, y);
            }

            // Draw pause indicator
            if (gameState.isPaused()) {
                g2d.setColor(new Color(255, 255, 0, 100));
                g2d.fillRect(5, 5, BOARD_WIDTH + 10, BOARD_HEIGHT + 10);
                g2d.setColor(Color.YELLOW);
                g2d.setFont(new Font("Arial", Font.BOLD, 30));
                FontMetrics fm = g2d.getFontMetrics();
                String text = "PAUSED";
                int x = (BOARD_WIDTH - fm.stringWidth(text)) / 2 + 10;
                int y = BOARD_HEIGHT / 2;
                g2d.drawString(text, x, y);
            }
        }
    }

    /**
     * Panel for displaying game statistics.
     */
    private static class StatsPanel extends JPanel {
        private GameState gameState;

        StatsPanel() {
            setBackground(new Color(40, 40, 40));
            setBorder(new EmptyBorder(10, 10, 10, 10));
        }

        void setGameState(GameState state) {
            this.gameState = state;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (gameState == null) {
                return;
            }

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Monospaced", Font.BOLD, 14));

            int y = 30;
            int lineHeight = 25;

            drawStat(g2d, "TETRIS", "", y);
            y += lineHeight * 1.5;

            drawStat(g2d, "Score", String.valueOf(gameState.getScore()), y);
            y += lineHeight;
            drawStat(g2d, "Lines", String.valueOf(gameState.getLinesCleared()), y);
            y += lineHeight;
            drawStat(g2d, "Level", gameState.getDifficulty().toString(), y);
            y += lineHeight;
            drawStat(g2d, "Gravity", String.valueOf(gameState.getDifficulty().getGravityTicks()), y);
            y += lineHeight;
            drawStat(g2d, "Ticks", String.valueOf(gameState.getTickCount()), y);
            y += lineHeight;
            drawStat(g2d, "Mult", String.format("%.1fx", gameState.getDifficulty().getScoreMultiplier()), y);

            if (gameState.isPaused()) {
                y += lineHeight * 2;
                g2d.setColor(Color.YELLOW);
                g2d.setFont(new Font("Monospaced", Font.BOLD, 16));
                drawStat(g2d, "[PAUSED]", "", y);
            }
        }

        /**
         * Draws a statistics label and value on the graphics context.
         * Helper method for rendering game statistics in the stats panel.
         *
         * @param g2d the Graphics2D context
         * @param label the label text
         * @param value the value to display
         * @param y the y-coordinate for drawing
         */
        private void drawStat(Graphics2D g2d, String label, String value, int y) {
            g2d.drawString(label + ":", 10, y);
            if (!value.isEmpty()) {
                g2d.drawString(value, 100, y);
            }
        }
    }
}
