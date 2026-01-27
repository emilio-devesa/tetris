package view;

import model.*;
import java.util.List;
import java.util.Set;

/**
 * Terminal renderer for the Tetris game.
 * Responsible for all visual output and formatting.
 *
 * Features:
 * - UTF-8 board rendering with Unicode characters
 * - Real-time game statistics display
 * - Difficulty level indicator
 * - High score display
 * - Game over display
 * - Help and control information
 */
public class Renderer {
    private static final String EMPTY_CELL = "□ ";
    private static final String FILLED_CELL = "■ ";
    private static final String PIECE_CELL = "◆ ";
    private static final String BORDER = "═";
    private static final String CORNER_TL = "╔";
    private static final String CORNER_TR = "╗";
    private static final String CORNER_BL = "╚";
    private static final String CORNER_BR = "╝";
    private static final String VERTICAL = "║";

    /**
     * Clears the terminal screen using ANSI escape codes.
     * Works on Unix-like systems (Linux, macOS) and Windows 10+.
     */
    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Renders the complete game state including board and statistics.
     *
     * @param state the current GameState to render
     */
    public void render(GameState state) {
        clearScreen();
        renderBoardWithStats(state);
    }

    /**
     * Renders the board and statistics side by side for compact display.
     *
     * @param state the current GameState
     */
    private void renderBoardWithStats(GameState state) {
        Board board = state.getBoard();
        Tetromino piece = state.getCurrentPiece();
        Set<Point> pieceCell = piece.getOccupiedCells();

        // Top border
        System.out.println(CORNER_TL + BORDER.repeat(Board.COLS * 2) + CORNER_TR);

        // Render board lines
        for (int row = 0; row < Board.ROWS; row++) {
            System.out.print(VERTICAL);
            for (int col = 0; col < Board.COLS; col++) {
                Point cell = new Point(row, col);
                if (pieceCell.contains(cell)) {
                    System.out.print(PIECE_CELL);
                } else if (board.isOccupied(cell)) {
                    System.out.print(FILLED_CELL);
                } else {
                    System.out.print(EMPTY_CELL);
                }
            }

            // Render side stats for selected rows
            System.out.print(VERTICAL);
            if (row == 0) {
                System.out.println(" TETRIS");
            } else if (row == 2) {
                System.out.printf(" Score:      %d%n", state.getScore());
            } else if (row == 3) {
                System.out.printf(" Lines:      %d%n", state.getLinesCleared());
            } else if (row == 4) {
                System.out.printf(" Difficulty: %s%n", getDifficultyName(state.getDifficulty()));
            } else if (row == 5) {
                System.out.printf(" Ticks:      %d%n", state.getTickCount());
            } else if (row == 6) {
                System.out.printf(" Gravity:    %d%n", state.getDifficulty().getGravityTicks());
            } else if (row == 7) {
                System.out.printf(" Multiplier: %.1fx%n", state.getDifficulty().getScoreMultiplier());
            } else {
                System.out.println();
            }
        }

        // Bottom border
        System.out.println(CORNER_BL + BORDER.repeat(Board.COLS * 2) + CORNER_BR);

        // Game over display
        if (state.isGameOver()) {
            renderGameOver(state);
        }

        // Control hints
        renderControls();
    }

    /**
     * Converts GameDifficulty to a display string with icon.
     *
     * @param difficulty the difficulty level
     * @return formatted difficulty name
     */
    private String getDifficultyName(GameDifficulty difficulty) {
        switch (difficulty) {
            case EASY:
                return "EASY   ◼";
            case NORMAL:
                return "NORMAL ◾";
            case HARD:
                return "HARD   ◾◾";
            case EXTREME:
                return "EXTREME◾◾◾";
            default:
                return difficulty.toString();
        }
    }

    /**
     * Renders the game over screen with final statistics.
     *
     * @param state the final game state
     */
    private void renderGameOver(GameState state) {
        System.out.println();
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║        GAME OVER                   ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.printf("║ Final Score:     %-18d ║%n", state.getScore());
        System.out.printf("║ Total Lines:     %-18d ║%n", state.getLinesCleared());
        System.out.printf("║ Total Ticks:     %-18d ║%n", state.getTickCount());
        System.out.printf("║ Difficulty:      %-18s ║%n", state.getDifficulty().toString());
        System.out.println("╚════════════════════════════════════╝");
    }

    /**
     * Renders control instructions.
     */
    private void renderControls() {
        System.out.println();
        System.out.println("Controls: LEFT | RIGHT | DOWN | ROTATE | DROP | QUIT");
    }

    /**
     * Renders a list of high scores.
     *
     * @param topScores list of high scores to display
     */
    public void renderHighScores(List<HighScoreManager.HighScore> topScores) {
        System.out.println();
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║        HIGH SCORES                 ║");
        System.out.println("╠════════════════════════════════════╣");

        if (topScores.isEmpty()) {
            System.out.println("║  No scores yet. Play a game!       ║");
        } else {
            int rank = 1;
            for (HighScoreManager.HighScore score : topScores) {
                System.out.printf("║ %2d. %6d - %-14s (L:%2d) ║%n",
                    rank++, score.getScore(), score.getDifficulty().name(), score.getLines());
                if (rank > 10) break; // Limit to top 10
            }
        }

        System.out.println("╚════════════════════════════════════╝");
    }

    /**
     * Renders game statistics summary.
     *
     * @param statistics the statistics to display
     */
    public void renderStatistics(GameStatistics statistics) {
        System.out.println();
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║        GAME STATISTICS             ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.printf("║ Final Score:    %-20d ║%n", statistics.getFinalScore());
        System.out.printf("║ Lines Cleared:  %-20d ║%n", statistics.getTotalLinesCleared());
        System.out.printf("║ Pieces Placed:  %-20d ║%n", statistics.getPiecesPlaced());
        System.out.printf("║ Duration:       %-6.1fs           ║%n", statistics.getPlayDurationSeconds());
        System.out.printf("║ Difficulty:     %-20s ║%n", statistics.getDifficulty().name());
        System.out.printf("║ Score/Line:     %-20.1f ║%n", statistics.getScorePerLine());
        System.out.printf("║ Score/Piece:    %-20.1f ║%n", statistics.getScorePerPiece());
        System.out.println("╚════════════════════════════════════╝");
    }

    /**
     * Renders a help message with all game controls.
     */
    public void renderHelp() {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║       TETRIS - Terminal Game       ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║ Controls:                          ║");
        System.out.println("║   LEFT   - Move piece left         ║");
        System.out.println("║   RIGHT  - Move piece right        ║");
        System.out.println("║   DOWN   - Accelerate piece        ║");
        System.out.println("║   ROTATE - Rotate piece (wall kick)║");
        System.out.println("║   DROP   - Instant drop            ║");
        System.out.println("║   QUIT   - Exit game               ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║ Symbols:                           ║");
        System.out.println("║   □ - Empty cell                   ║");
        System.out.println("║   ■ - Fixed piece                  ║");
        System.out.println("║   ◆ - Falling piece                ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║ Tips:                              ║");
        System.out.println("║ - Use wall kick to rotate at edges ║");
        System.out.println("║ - Higher difficulty = more points  ║");
        System.out.println("║ - Complete rows to earn points     ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.println();
    }
}
