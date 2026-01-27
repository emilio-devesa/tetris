package model;

import java.util.HashSet;
import java.util.Set;

/**
 * Immutable representation of the game board.
 * Tracks which cells are occupied by fixed pieces.
 * 
 * Board dimensions: ROWS x COLS (standard 10x10 for this implementation)
 */
public class Board {
    public static final int ROWS = 10;
    public static final int COLS = 10;

    private final Set<Point> occupiedCells;

    public Board() {
        this.occupiedCells = new HashSet<>();
    }

    private Board(Set<Point> occupiedCells) {
        this.occupiedCells = new HashSet<>(occupiedCells);
    }

    /**
     * Returns true if the given cell is within board bounds.
     */
    public boolean isInBounds(Point cell) {
        return cell.getRow() >= 0 && cell.getRow() < ROWS &&
               cell.getCol() >= 0 && cell.getCol() < COLS;
    }

    /**
     * Returns true if the given cell is occupied by a fixed piece.
     */
    public boolean isOccupied(Point cell) {
        return occupiedCells.contains(cell);
    }

    /**
     * Returns true if all cells are within bounds and not occupied.
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
     */
    public Board addPiece(Set<Point> cells) {
        Set<Point> newOccupied = new HashSet<>(occupiedCells);
        newOccupied.addAll(cells);
        return new Board(newOccupied);
    }

    /**
     * Returns the set of occupied cells (immutable view).
     */
    public Set<Point> getOccupiedCells() {
        return new HashSet<>(occupiedCells);
    }

    /**
     * Returns rows that are completely filled.
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
     * Creates a new board with the given rows removed.
     * Cells above removed rows are shifted down.
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
            
            // Count how many cleared rows are above this cell
            int shiftDown = 0;
            for (Integer clearedRow : rowsToClear) {
                if (clearedRow < cell.getRow()) {
                    shiftDown++;
                }
            }
            
            newOccupied.add(new Point(cell.getRow() + shiftDown, cell.getCol()));
        }
        
        return new Board(newOccupied);
    }

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
