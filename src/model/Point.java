package model;

/**
 * Represents a 2D point on the game board.
 * Immutable class for coordinate representation.
 *
 * Point uses 0-based indexing where (0, 0) is the top-left corner.
 * Row increases downward, column increases rightward.
 *
 * @author Tetris Engine
 */
public class Point {
    private final int row;
    private final int col;

    /**
     * Constructs a Point with the specified row and column coordinates.
     *
     * @param row the row coordinate (0 = top)
     * @param col the column coordinate (0 = left)
     */
    public Point(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Returns the row coordinate of this point.
     *
     * @return the row coordinate
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column coordinate of this point.
     *
     * @return the column coordinate
     */
    public int getCol() {
        return col;
    }

    /**
     * Returns a new Point translated by the given offsets.
     * This Point remains unchanged (immutability).
     *
     * @param dRow the row offset (positive = down, negative = up)
     * @param dCol the column offset (positive = right, negative = left)
     * @return a new Point at the translated position
     */
    public Point translate(int dRow, int dCol) {
        return new Point(row + dRow, col + dCol);
    }

    /**
     * Compares this Point with another object for equality.
     * Two Points are equal if they have the same row and column coordinates.
     *
     * @param o the object to compare with
     * @return true if both Points have the same coordinates, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return row == point.row && col == point.col;
    }

    /**
     * Returns the hash code for this Point.
     * Allows Points to be used in HashSet and HashMap.
     *
     * @return hash code based on row and column
     */
    @Override
    public int hashCode() {
        return 31 * row + col;
    }

    /**
     * Returns a string representation of this Point.
     *
     * @return string in format "(row, col)"
     */
    @Override
    public String toString() {
        return String.format("(%d, %d)", row, col);
    }
}
