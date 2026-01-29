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
public class Renderer implements GameView {
    private final java.util.Scanner scanner;

    /**
     * Constructs a new Renderer with input scanner.
     */
    public Renderer() {
        this.scanner = new java.util.Scanner(System.in);
    }
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
            } else if (row == 9 && state.isPaused()) {
                System.out.println(" [PAUSED]");
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
    @Override
    public void renderGameOver(GameState state) {
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
        System.out.println("Controls: LEFT | RIGHT | DOWN | ROTATE | DROP | PAUSE | QUIT");
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
        System.out.println("║   A / LEFT   - Move piece left     ║");
        System.out.println("║   D / RIGHT  - Move piece right    ║");
        System.out.println("║   S / DOWN   - Accelerate piece    ║");
        System.out.println("║   W/R/ ROTATE- Rotate (wall kick)  ║");
        System.out.println("║   SPACE/DROP - Instant drop        ║");
        System.out.println("║   P / PAUSE  - Pause game          ║");
        System.out.println("║   Q / QUIT   - Exit game           ║");
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

    /**
     * Reads a single input action from the user (non-blocking).
     * Maps keyboard input to game actions.
     *
     * @return the GameAction input, or null if no input available
     */
    @Override
    public GameAction readInput() {
        if (!scanner.hasNextLine()) {
            return null;
        }

        String input = scanner.nextLine().trim().toUpperCase();
        if (input.isEmpty()) {
            return null;
        }

        return parseInput(input);
    }

    /**
     * Parses input string to GameAction.
     *
     * @param input the input string (single character or full word)
     * @return the corresponding GameAction, or null if unrecognized
     */
    private GameAction parseInput(String input) {
        switch (input) {
            case "A":
            case "LEFT":
                return GameAction.LEFT;
            case "D":
            case "RIGHT":
                return GameAction.RIGHT;
            case "S":
            case "DOWN":
                return GameAction.DOWN;
            case "W":
            case "ROTATE":
                return GameAction.ROTATE;
            case " ":
            case "SPACE":
            case "DROP":
                return GameAction.DROP;
            case "P":
            case "PAUSE":
                return GameAction.PAUSE;
            case "Q":
            case "QUIT":
                return GameAction.QUIT;
            default:
                return null;
        }
    }

    /**
     * Closes the renderer and its resources.
     */
    @Override
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }

    /**
     * Shows the main menu in terminal mode.
     *
     * @return selected menu option
     */
    @Override
    public int showMainMenu() {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║     TETRIS - Main Menu             ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║ 1 - Play Game                      ║");
        System.out.println("║ 2 - View High Scores               ║");
        System.out.println("║ 3 - Watch Demo                     ║");
        System.out.println("║ 4 - Exit                           ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.print("Choose (1-4): ");

        try {
            java.util.Scanner tempScanner = new java.util.Scanner(System.in);
            return Integer.parseInt(tempScanner.nextLine().trim());
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * Shows difficulty selection in terminal mode.
     *
     * @return selected GameDifficulty
     */
    @Override
    public GameDifficulty selectDifficulty() {
        java.util.Scanner tempScanner = new java.util.Scanner(System.in);
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║     SELECT DIFFICULTY              ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║ 1 - EASY   (slowest, no multiplier)║");
        System.out.println("║ 2 - NORMAL (balanced gameplay)    ║");
        System.out.println("║ 3 - HARD   (faster, 1.5x bonus)  ║");
        System.out.println("║ 4 - EXTREME(fastest, 2.0x bonus) ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.print("Choose (1-4, default=2): ");

        try {
            String input = tempScanner.nextLine().trim();
            switch (input) {
                case "1":
                    return GameDifficulty.EASY;
                case "3":
                    return GameDifficulty.HARD;
                case "4":
                    return GameDifficulty.EXTREME;
                case "2":
                default:
                    return GameDifficulty.NORMAL;
            }
        } catch (Exception e) {
            return GameDifficulty.NORMAL;
        }
    }

    /**
     * Shows soundtrack selection in terminal mode.
     *
     * @return selected Soundtrack
     */
    @Override
    public AudioManager.Soundtrack selectSoundtrack() {
        java.util.Scanner tempScanner = new java.util.Scanner(System.in);
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║     SELECT BACKGROUND MUSIC        ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║ 1 - Loginska                       ║");
        System.out.println("║ 2 - Bradinsky                      ║");
        System.out.println("║ 3 - Karinka                        ║");
        System.out.println("║ 4 - Troika                         ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.print("Choose (1-4, default=1): ");

        try {
            String input = tempScanner.nextLine().trim();
            switch (input) {
                case "1":
                    return AudioManager.Soundtrack.MUSIC_1;
                case "2":
                    return AudioManager.Soundtrack.MUSIC_2;
                case "3":
                    return AudioManager.Soundtrack.MUSIC_3;
                case "4":
                    return AudioManager.Soundtrack.MUSIC_4;
                default:
                    return AudioManager.Soundtrack.MUSIC_1;
            }
        } catch (Exception e) {
            return AudioManager.Soundtrack.MUSIC_1;
        }
    }
}
