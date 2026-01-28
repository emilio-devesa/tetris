package model;

import java.util.HashSet;
import java.util.Set;

/**
 * Immutable representation of the game board.
 * Tracks which cells are occupied by fixed pieces.
 *
 * Board dimensions: 10 rows × 10 columns (standard Tetris layout)
 * - Row 0 is the top, Row 9 is the bottom
 * - Column 0 is the left, Column 9 is the right
 *
 * The board state is immutable: operations create new Board instances
 * rather than modifying the existing one.
 *
 * @author Tetris Engine
 */
public class Board {
    /** Number of rows on the board */
    public static final int ROWS = 10;
    /** Number of columns on the board */
    public static final int COLS = 10;

    private final Set<Point> occupiedCells;

    /**
     * Constructs an empty board with no occupied cells.
     */
    public Board() {
        this.occupiedCells = new HashSet<>();
    }

    /**
     * Private constructor for creating a board with specific occupied cells.
     * Used internally for immutable operations.
     *
     * @param occupiedCells the set of occupied cell coordinates
     */
    private Board(Set<Point> occupiedCells) {
        this.occupiedCells = new HashSet<>(occupiedCells);
    }

    /**
     * Checks if the given cell is within board boundaries.
     *
     * @param cell the Point to check
     * @return true if cell is within bounds (0 ≤ row < ROWS, 0 ≤ col < COLS)
     */
    public boolean isInBounds(Point cell) {
        return cell.getRow() >= 0 && cell.getRow() < ROWS &&
               cell.getCol() >= 0 && cell.getCol() < COLS;
    }

    /**
     * Checks if the given cell is occupied by a fixed piece.
     *
     * @param cell the Point to check
     * @return true if cell is occupied
     */
    public boolean isOccupied(Point cell) {
        return occupiedCells.contains(cell);
    }

    /**
     * Checks if a tetromino can be placed at the given cells.
     * A piece can be placed if all cells are within bounds and not occupied.
     *
     * @param cells the set of Points the tetromino would occupy
     * @return true if all cells are valid placement locations
     */
    public boolean canPlacePiece(Set<Point> cells) {
        for (Point cell : cells) {
            if (!isInBounds(cell) || isOccupied(cell)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new board with the given cells marked as occupied.
     * This board remains unchanged (immutability).
     *
     * @param cells the cells to mark as occupied
     * @return a new Board with the cells added
     */
    public Board addPiece(Set<Point> cells) {
        Set<Point> newOccupied = new HashSet<>(occupiedCells);
        newOccupied.addAll(cells);
        return new Board(newOccupied);
    }

    /**
     * Returns an immutable view of all occupied cells on this board.
     *
     * @return a Set of occupied Points
     */
    public Set<Point> getOccupiedCells() {
        return new HashSet<>(occupiedCells);
    }

    /**
     * Finds all rows that are completely filled.
     * A row is complete when all 10 columns are occupied.
     *
     * @return a Set of row indices that are completely filled
     */
    public Set<Integer> getCompleteRows() {
        Set<Integer> completeRows = new HashSet<>();

        for (int row = 0; row < ROWS; row++) {
            int filledCells = 0;
            for (int col = 0; col < COLS; col++) {
                if (isOccupied(new Point(row, col))) {
                    filledCells++;
                }
            }
            if (filledCells == COLS) {
                completeRows.add(row);
            }
        }

        return completeRows;
    }

    /**
     * Creates a new board with the given rows removed and cells above shifted down.
     * This board remains unchanged (immutability).
     *
     * Clearing rows removes all cells in those rows and shifts higher rows down,
     * filling the gap left by cleared rows.
     *
     * @param rowsToClear the set of row indices to remove
     * @return a new Board with rows cleared and cells shifted
     */
    public Board clearRows(Set<Integer> rowsToClear) {
        if (rowsToClear.isEmpty()) {
            return new Board(occupiedCells);
        }

        Set<Point> newOccupied = new HashSet<>();

        for (Point cell : occupiedCells) {
            if (rowsToClear.contains(cell.getRow())) {
                continue; // Skip cells in rows to clear
            }

            // Count how many cleared rows are below this cell (row index > cell row)
            // Each cleared row below causes this cell to fall down by 1
            long shiftDown = rowsToClear.stream()
                    .filter(clearedRow -> clearedRow > cell.getRow())
                    .count();

            newOccupied.add(new Point(cell.getRow() + (int) shiftDown, cell.getCol()));
        }

        return new Board(newOccupied);
    }

    /**
     * Returns a visual representation of the board as a string.
     * Uses ■ for occupied cells and □ for empty cells.
     *
     * @return string representation of the board
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                sb.append(isOccupied(new Point(row, col)) ? "■ " : "□ ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
