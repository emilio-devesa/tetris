package view;

import model.*;
import model.Point;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
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

    private static final int CELL_SIZE = 30;
    private static final int BOARD_WIDTH = Board.COLS * CELL_SIZE;
    private static final int BOARD_HEIGHT = Board.ROWS * CELL_SIZE;

    /**
     * Constructs a new SwingRenderer with Swing UI.
     */
    public SwingRenderer() {
        this.inputQueue = new LinkedBlockingQueue<>();

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

        addKeyListener(this);
        setFocusable(true);
    }

    @Override
    public void render(GameState state) {
        gamePanel.setGameState(state);
        statsPanel.setGameState(state);
        SwingUtilities.invokeLater(() -> {
            gamePanel.repaint();
            statsPanel.repaint();
        });
    }

    @Override
    public GameAction readInput() {
        return inputQueue.poll();
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

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        GameAction action = null;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                action = GameAction.LEFT;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                action = GameAction.RIGHT;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                action = GameAction.DOWN;
                break;
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                action = GameAction.ROTATE;
                break;
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

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }

    /**
     * Panel for rendering the game board.
     */
    private static class GamePanel extends JPanel {
        private GameState gameState;
        private boolean gameOver = false;

        private static final Color COLOR_EMPTY = new Color(50, 50, 50);
        private static final Color COLOR_FILLED = new Color(100, 150, 255);
        private static final Color COLOR_FALLING = new Color(255, 200, 0);
        private static final Color COLOR_BORDER = new Color(150, 150, 150);

        GamePanel() {
            setBackground(COLOR_EMPTY);
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

                    Point cell = new Point(row, col);
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

        private void drawStat(Graphics2D g2d, String label, String value, int y) {
            g2d.drawString(label + ":", 10, y);
            if (!value.isEmpty()) {
                g2d.drawString(value, 100, y);
            }
        }
    }
}
