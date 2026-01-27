package view;

import model.*;
import java.util.Set;

/**
 * Renders the game state to the terminal.
 * Responsible for all visual output of the game.
 */
public class Renderer {
    private static final String EMPTY_CELL = "□ ";
    private static final String FILLED_CELL = "■ ";
    private static final String PIECE_CELL = "◆ ";
    private static final String BORDER = "═";
    private static final String BORDER_CORNER = "╔╗╚╝";

    /**
     * Clears the terminal screen.
     */
    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Renders the current game state.
     */
    public void render(GameState state) {
        clearScreen();
        
        System.out.println("╔" + BORDER.repeat(Board.COLS * 2) + "╗");
        renderBoard(state);
        System.out.println("╚" + BORDER.repeat(Board.COLS * 2) + "╝");
        
        renderStats(state);
    }

    /**
     * Renders the game board with current piece.
     */
    private void renderBoard(GameState state) {
        Board board = state.getBoard();
        Tetromino piece = state.getCurrentPiece();
        Set<Point> pieceCell = piece.getOccupiedCells();

        for (int row = 0; row < Board.ROWS; row++) {
            System.out.print("║");
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
            System.out.println("║");
        }
    }

    /**
     * Renders game statistics.
     */
    private void renderStats(GameState state) {
        System.out.println();
        System.out.println("Score:        " + state.getScore());
        System.out.println("Lines Cleared: " + state.getLinesCleared());
        System.out.println("Tick:         " + state.getTickCount());
        
        if (state.isGameOver()) {
            System.out.println();
            System.out.println("╔════════════════════════╗");
            System.out.println("║     GAME OVER!         ║");
            System.out.println("╚════════════════════════╝");
        }
    }

    /**
     * Renders a simple help message.
     */
    public void renderHelp() {
        System.out.println("Tetris Controls:");
        System.out.println("  LEFT   - Move piece left");
        System.out.println("  RIGHT  - Move piece right");
        System.out.println("  DOWN   - Move piece down");
        System.out.println("  ROTATE - Rotate piece");
        System.out.println("  DROP   - Instant drop");
        System.out.println("  QUIT   - Exit game");
        System.out.println();
    }
}
