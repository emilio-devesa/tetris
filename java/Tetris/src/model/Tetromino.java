package model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a tetromino piece (L-shaped) on the game board.
 * Immutable class with support for 4 rotation states (0-3).
 *
 * The L-tetromino occupies 4 blocks in one of these patterns:
 * <pre>
 * Rotation 0:     Rotation 1:     Rotation 2:     Rotation 3:
 *   X             X X             X X X           X
 *   X X X         X               X         X X X X X
 *                 X X             X X X     X
 * </pre>
 *
 * @author Tetris Engine
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

    /**
     * Constructs a tetromino at the given position with specified rotation.
     *
     * @param position       the top-left corner of the bounding box
     * @param rotationState  the rotation state (0-3, automatically normalized to 0-3)
     */
    public Tetromino(Point position, int rotationState) {
        this.position = position;
        this.rotationState = rotationState % 4;
    }

    /**
     * Constructs a tetromino at the given position with rotation 0.
     *
     * @param position the top-left corner of the bounding box
     */
    public Tetromino(Point position) {
        this(position, 0);
    }

    /**
     * Returns the absolute coordinates of all blocks that form this tetromino.
     * Each block's position is calculated by adding the bounding box offset
     * to the tetromino's position.
     *
     * @return a set of Points representing occupied cells
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
     * Creates a new tetromino at the specified position.
     * Maintains the current rotation state.
     * The original tetromino is unchanged (immutability).
     *
     * @param newPosition the new position
     * @return a new Tetromino at the specified position
     */
    public Tetromino moveTo(Point newPosition) {
        return new Tetromino(newPosition, rotationState);
    }

    /**
     * Creates a new tetromino rotated clockwise.
     * Advances the rotation state by one (0→1→2→3→0).
     * The original tetromino is unchanged (immutability).
     *
     * @return a new Tetromino rotated clockwise
     */
    public Tetromino rotateClockwise() {
        return new Tetromino(position, (rotationState + 1) % 4);
    }

    /**
     * Returns the width of the bounding box (always 4 for L-tetromino).
     *
     * @return bounding box width
     */
    public int getBoundingBoxWidth() {
        return 4;
    }

    /**
     * Returns the height of the bounding box (always 4 for L-tetromino).
     *
     * @return bounding box height
     */
    public int getBoundingBoxHeight() {
        return 4;
    }

    /**
     * Returns the position (top-left corner of bounding box) of this tetromino.
     *
     * @return the position Point
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Returns the current rotation state (0-3).
     *
     * @return rotation state
     */
    public int getRotationState() {
        return rotationState;
    }

    /**
     * Returns a string representation of this tetromino.
     *
     * @return string with position and rotation information
     */
    @Override
    public String toString() {
        return String.format("Tetromino(pos=%s, rotation=%d)", position, rotationState);
    }
}
