package model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a tetromino piece (currently L-shaped).
 * Immutable representation with support for rotation.
 * 
 * The L-tetromino looks like:
 *   X
 *   X X X
 */
public class Tetromino {
    private static final int[][] L_ROTATION_0 = {
        {0, 0}, {1, 0}, {1, 1}, {1, 2}
    };
    private static final int[][] L_ROTATION_1 = {
        {0, 1}, {0, 2}, {1, 1}, {2, 1}
    };
    private static final int[][] L_ROTATION_2 = {
        {0, 0}, {0, 1}, {0, 2}, {1, 2}
    };
    private static final int[][] L_ROTATION_3 = {
        {0, 1}, {1, 1}, {2, 0}, {2, 1}
    };

    private static final int[][][] ROTATIONS = {
        L_ROTATION_0, L_ROTATION_1, L_ROTATION_2, L_ROTATION_3
    };

    private final Point position;  // Top-left corner of bounding box
    private final int rotationState; // 0-3

    public Tetromino(Point position, int rotationState) {
        this.position = position;
        this.rotationState = rotationState % 4;
    }

    /**
     * Creates a tetromino at the given position with rotation 0.
     */
    public Tetromino(Point position) {
        this(position, 0);
    }

    /**
     * Returns the absolute coordinates of the blocks that form this tetromino.
     */
    public Set<Point> getOccupiedCells() {
        Set<Point> cells = new HashSet<>();
        int[][] pattern = ROTATIONS[rotationState];
        
        for (int[] offset : pattern) {
            int row = position.getRow() + offset[0];
            int col = position.getCol() + offset[1];
            cells.add(new Point(row, col));
        }
        
        return cells;
    }

    /**
     * Creates a new tetromino moved to a new position.
     */
    public Tetromino moveTo(Point newPosition) {
        return new Tetromino(newPosition, rotationState);
    }

    /**
     * Creates a new tetromino with the next rotation state.
     */
    public Tetromino rotateClockwise() {
        return new Tetromino(position, (rotationState + 1) % 4);
    }

    /**
     * Returns the bounding box of the tetromino.
     * Used for collision detection.
     */
    public int getBoundingBoxWidth() {
        return 4; // L-tetromino bounding box is 3x4
    }

    public int getBoundingBoxHeight() {
        return 4;
    }

    public Point getPosition() {
        return position;
    }

    public int getRotationState() {
        return rotationState;
    }

    @Override
    public String toString() {
        return String.format("Tetromino(pos=%s, rotation=%d)", position, rotationState);
    }
}
